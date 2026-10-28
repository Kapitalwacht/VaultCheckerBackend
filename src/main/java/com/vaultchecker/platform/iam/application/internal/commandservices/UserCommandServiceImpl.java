package com.vaultchecker.platform.iam.application.internal.commandservices;

import com.vaultchecker.platform.iam.application.commandservices.UserCommandService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.identity.GoogleIdentityService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.notifications.NotificationService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.commands.ChangeEmailCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RequestPhoneLoginCodeCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInWithGoogleCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInWithPhoneCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignUpCommand;
import com.vaultchecker.platform.iam.domain.model.commands.UpdateProfileCommand;
import com.vaultchecker.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vaultchecker.platform.iam.domain.model.commands.VerifyRecoveryCodeCommand;
import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.iam.domain.model.events.UserSignedUpEvent;
import com.vaultchecker.platform.iam.domain.model.valueobjects.RoleNameMapper;
import com.vaultchecker.platform.iam.domain.model.valueobjects.Roles;
import com.vaultchecker.platform.iam.domain.repositories.RoleRepository;
import com.vaultchecker.platform.iam.domain.repositories.UserRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final Duration RECOVERY_CODE_TTL = Duration.ofMinutes(15);
    private static final Duration LOGIN_CODE_TTL = Duration.ofMinutes(5);

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;
    private final GoogleIdentityService googleIdentityService;
    private final String frontendBaseUrl;
    private final SecureRandom random = new SecureRandom();

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository,
            ApplicationEventPublisher eventPublisher,
            NotificationService notificationService,
            GoogleIdentityService googleIdentityService,
            @Value("${frontend.base-url}") String frontendBaseUrl) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
        this.notificationService = notificationService;
        this.googleIdentityService = googleIdentityService;
        this.frontendBaseUrl = frontendBaseUrl;
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

        var role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        var user = new User(command.email(), hashingService.encode(command.password()), List.of(role));
        user.setName(command.name());
        user.setPhone(command.phone());
        user.setStoreId(command.storeId());
        var verificationToken = generateToken();
        user.issueVerificationToken(verificationToken);
        userRepository.save(user);
        return userRepository.findByEmail(command.email())
                .<Result<User, ApplicationError>>map(savedUser -> {
                    eventPublisher.publishEvent(new UserSignedUpEvent(
                            savedUser.getId(), savedUser.getEmail(), roleName.name()));
                    notificationService.sendVerificationEmail(savedUser.getEmail(), savedUser.getName(),
                            verificationLink(verificationToken, savedUser.getEmail()));
                    return Result.success(savedUser);
                })
                .orElseGet(() -> Result.failure(ApplicationError.unexpected("sign-up", "Created user could not be reloaded")));
    }

    @Override
    public Result<User, ApplicationError> handle(VerifyEmailCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty() || !user.get().confirmEmail(command.token().trim())) {
            return Result.failure(ApplicationError.validationError("token", "Invalid verification token"));
        }
        return Result.success(userRepository.save(user.get()));
    }

    @Override
    public Result<User, ApplicationError> handle(RequestPasswordRecoveryCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.email()));
        }
        var code = generateCode();
        user.get().issueRecoveryCode(code, expiryAfter(RECOVERY_CODE_TTL));
        var saved = userRepository.save(user.get());
        if (!notificationService.sendRecoveryCode(saved.getEmail(), saved.getName(), code)) {
            return Result.failure(ApplicationError.serviceUnavailable("email", "Recovery email could not be sent"));
        }
        return Result.success(saved);
    }

    @Override
    public Result<User, ApplicationError> handle(VerifyRecoveryCodeCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty() || !user.get().hasRecoveryCode(command.code().trim())) {
            return Result.failure(ApplicationError.validationError("code", "Invalid recovery code"));
        }
        if (user.get().isRecoveryCodeExpired(System.currentTimeMillis())) {
            return Result.failure(ApplicationError.businessRuleViolation("code-expired", "Recovery code expired"));
        }
        return Result.success(user.get());
    }

    @Override
    public Result<User, ApplicationError> handle(ResetPasswordCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty() || !user.get().hasRecoveryCode(command.code().trim())) {
            return Result.failure(ApplicationError.validationError("code", "Invalid recovery code"));
        }
        if (user.get().isRecoveryCodeExpired(System.currentTimeMillis())) {
            return Result.failure(ApplicationError.businessRuleViolation("code-expired", "Recovery code expired"));
        }
        user.get().resetPassword(hashingService.encode(command.newPassword()));
        return Result.success(userRepository.save(user.get()));
    }

    @Override
    public Result<String, ApplicationError> handle(RequestPhoneLoginCodeCommand command) {
        var user = userRepository.findByPhone(command.phone());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Phone", command.phone()));
        }
        var code = generateCode();
        user.get().issueLoginCode(code, expiryAfter(LOGIN_CODE_TTL));
        userRepository.save(user.get());
        return Result.success(code);
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInWithGoogleCommand command) {
        var email = googleIdentityService.verifiedEmail(command.accessToken());
        if (email.isEmpty()) {
            return Result.failure(ApplicationError.validationError("googleToken", "Invalid Google access token"));
        }
        var user = userRepository.findByEmail(email.get());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", email.get()));
        }
        var token = tokenService.generateToken(user.get().getEmail());
        return Result.success(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInWithPhoneCommand command) {
        var user = userRepository.findByPhone(command.phone());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Phone", command.phone()));
        }
        if (!user.get().hasLoginCode(command.code().trim())) {
            return Result.failure(ApplicationError.validationError("code", "Invalid login code"));
        }
        if (user.get().isLoginCodeExpired(System.currentTimeMillis())) {
            return Result.failure(ApplicationError.businessRuleViolation("code-expired", "Login code expired"));
        }
        user.get().clearLoginCode();
        var saved = userRepository.save(user.get());
        return Result.success(ImmutablePair.of(saved, tokenService.generateToken(saved.getEmail())));
    }

    @Override
    public Result<User, ApplicationError> handle(UpdateProfileCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.email()));
        }
        user.get().updateProfile(command.name(), command.phone());
        return Result.success(userRepository.save(user.get()));
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(ChangeEmailCommand command) {
        var user = userRepository.findByEmail(command.currentEmail());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.currentEmail()));
        }
        var newEmail = command.newEmail().trim();
        if (!newEmail.equalsIgnoreCase(command.currentEmail()) && userRepository.existsByEmail(newEmail)) {
            return Result.failure(ApplicationError.conflict("User", "Email already exists"));
        }
        var verificationToken = generateToken();
        if (!notificationService.sendVerificationEmail(newEmail, user.get().getName(),
                verificationLink(verificationToken, newEmail))) {
            return Result.failure(ApplicationError.serviceUnavailable("email", "Verification email could not be sent"));
        }
        user.get().changeEmail(newEmail, verificationToken);
        var saved = userRepository.save(user.get());
        return Result.success(ImmutablePair.of(saved, tokenService.generateToken(saved.getEmail())));
    }

    @Override
    public Result<User, ApplicationError> handle(ChangePasswordCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.email()));
        }
        if (!hashingService.matches(command.currentPassword(), user.get().getPassword())) {
            return Result.failure(ApplicationError.validationError("currentPassword", "Current password is incorrect"));
        }
        user.get().setPassword(hashingService.encode(command.newPassword()));
        return Result.success(userRepository.save(user.get()));
    }

    private Roles resolveRoleName(String requestedRole) {

        return RoleNameMapper.fromClientRole(requestedRole, Role.getDefaultRole().getName());
    }

    private String generateCode() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private String generateToken() {
        var bytes = new byte[16];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static long expiryAfter(Duration ttl) {
        return System.currentTimeMillis() + ttl.toMillis();
    }

    private String verificationLink(String token, String email) {
        return "%s/verify?token=%s&email=%s".formatted(
                frontendBaseUrl, token, URLEncoder.encode(email, StandardCharsets.UTF_8));
    }
}
