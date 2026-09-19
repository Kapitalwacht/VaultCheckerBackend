package com.vaultchecker.platform.iam.application.internal.commandservices;

import com.vaultchecker.platform.iam.application.commandservices.UserCommandService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.commands.SignInCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignUpCommand;
import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.iam.domain.model.events.UserSignedUpEvent;
import com.vaultchecker.platform.iam.domain.model.valueobjects.Roles;
import com.vaultchecker.platform.iam.domain.repositories.RoleRepository;
import com.vaultchecker.platform.iam.domain.repositories.UserRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User command service implementation.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.email()));
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            return Result.failure(ApplicationError.validationError("credentials", "Invalid email or password"));
        }
        var token = tokenService.generateToken(user.get().getEmail());
        return Result.success(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            return Result.failure(ApplicationError.conflict("User", "Email already exists"));
        }

        var roleName = resolveRoleName(command.roleName());
        // Get-or-create: the role is persisted the first time it is used, so no startup
        // seeding of hardcoded data is required.
        var role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        var user = new User(command.email(), hashingService.encode(command.password()), List.of(role));
        user.setName(command.name());
        user.setPhone(command.phone());
        user.setStoreId(command.storeId());
        user.setEmailVerified(false);
        userRepository.save(user);
        return userRepository.findByEmail(command.email())
                .<Result<User, ApplicationError>>map(savedUser -> {
                    eventPublisher.publishEvent(new UserSignedUpEvent(
                            savedUser.getId(), savedUser.getEmail(), roleName.name()));
                    return Result.success(savedUser);
                })
                .orElseGet(() -> Result.failure(ApplicationError.unexpected("sign-up", "Created user could not be reloaded")));
    }

    private Roles resolveRoleName(String requestedRole) {
        if (requestedRole == null || requestedRole.isBlank()) {
            return Role.getDefaultRole().getName();
        }
        try {
            return Roles.valueOf(requestedRole.trim());
        } catch (IllegalArgumentException ignored) {
            return Role.getDefaultRole().getName();
        }
    }
}
