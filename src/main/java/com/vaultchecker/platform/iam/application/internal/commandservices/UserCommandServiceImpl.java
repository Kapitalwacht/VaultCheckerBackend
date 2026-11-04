package com.vaultchecker.platform.iam.application.internal.commandservices;

import com.vaultchecker.platform.iam.application.commandservices.UserCommandService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.customers.CustomerDirectoryService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.identity.GoogleIdentityService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.notifications.NotificationService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.stores.StoreDirectoryService;
import com.vaultchecker.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.commands.ChangeEmailCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RefreshSessionCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ResendVerificationCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInWithGoogleCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignOutCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignUpCommand;
import com.vaultchecker.platform.iam.domain.model.commands.UpdateProfileCommand;
import com.vaultchecker.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vaultchecker.platform.iam.domain.model.commands.VerifyRecoveryCodeCommand;
import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.iam.domain.model.events.UserSignedUpEvent;
import com.vaultchecker.platform.iam.domain.model.valueobjects.AuthenticatedSession;
import com.vaultchecker.platform.iam.domain.model.valueobjects.RoleNameMapper;
import com.vaultchecker.platform.shared.domain.model.events.AuditableActionRegisteredEvent;
import com.vaultchecker.platform.iam.domain.model.valueobjects.Roles;
import com.vaultchecker.platform.iam.domain.repositories.RoleRepository;
import com.vaultchecker.platform.iam.domain.repositories.UserRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final Duration RECOVERY_CODE_TTL = Duration.ofMinutes(15);
    private static final String USER_SIGNED_IN_ACTION = "USER_SIGNED_IN";
    private static final String USER_SIGNED_OUT_ACTION = "USER_SIGNED_OUT";
    private static final String REFRESH_TOKEN_DIGEST = "SHA-256";
    private static final String PASSWORD_RESET_ACTION = "PASSWORD_RESET";

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;
    private final GoogleIdentityService googleIdentityService;
    private final CustomerDirectoryService customerDirectoryService;
    private final StoreDirectoryService storeDirectoryService;
    private final String frontendBaseUrl;
    private final Duration refreshTokenTtl;
    private final boolean emailVerificationRequired;
    private final SecureRandom random = new SecureRandom();

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository,
            ApplicationEventPublisher eventPublisher,
            NotificationService notificationService,
            GoogleIdentityService googleIdentityService,
            CustomerDirectoryService customerDirectoryService,
            StoreDirectoryService storeDirectoryService,
            @Value("${frontend.base-url}") String frontendBaseUrl,
            @Value("${authorization.refresh.expiration.days}") int refreshTokenDays,
            @Value("${app.email-verification.required}") boolean emailVerificationRequired) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
        this.notificationService = notificationService;
        this.googleIdentityService = googleIdentityService;
        this.customerDirectoryService = customerDirectoryService;
        this.storeDirectoryService = storeDirectoryService;
        this.frontendBaseUrl = frontendBaseUrl;
        this.refreshTokenTtl = Duration.ofDays(refreshTokenDays);
        this.emailVerificationRequired = emailVerificationRequired;
    }

    @Override
    public Result<AuthenticatedSession, ApplicationError> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.email()));
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            return Result.failure(ApplicationError.validationError("credentials", "error.detail.invalid-credentials"));
        }
        if (emailVerificationRequired && !user.get().isEmailVerified()) {
            return Result.failure(ApplicationError.forbidden("error.detail.email-not-verified"));
        }
        return openSession(user.get(), true);
    }

    @Override
    public Result<AuthenticatedSession, ApplicationError> handle(RefreshSessionCommand command) {
        var user = userRepository.findByRefreshTokenHash(hashOf(command.refreshToken()));
        if (user.isEmpty() || user.get().isRefreshTokenExpired(System.currentTimeMillis())) {
            return Result.failure(ApplicationError.validationError("refreshToken", "error.detail.invalid-refresh-token"));
        }
        return openSession(user.get(), false);
    }

    @Override
    public Result<Boolean, ApplicationError> handle(SignOutCommand command) {
        userRepository.findByRefreshTokenHash(hashOf(command.refreshToken())).ifPresent(user -> {
            user.revokeRefreshToken();
            userRepository.save(user);
            recordAction(user, USER_SIGNED_OUT_ACTION, "User %s signed out".formatted(user.getEmail()));
        });
        return Result.success(true);
    }

    @Override
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            return Result.failure(ApplicationError.conflict("User", "error.detail.email-taken"));
        }

        var roleName = resolveRoleName(command.roleName());
        if (roleName == Roles.ROLE_SYSTEM_ADMIN && systemAdminExists()) {
            return Result.failure(ApplicationError.forbidden("error.detail.system-admin-exists"));
        }

        if (roleName == Roles.ROLE_CUSTOMER && (command.dni() == null || command.dni().isBlank())) {
            return Result.failure(ApplicationError.validationError("dni", "error.detail.dni-required"));
        }
        if (roleName == Roles.ROLE_CUSTOMER && dniTaken(command.dni().trim())) {
            return Result.failure(ApplicationError.conflict("UserDni", "error.detail.dni-taken"));
        }

        var storeId = command.storeId();
        boolean registersStore = roleName == Roles.ROLE_STORE_ADMIN && storeId != null && !storeId.isBlank();
        if (registersStore && storeDirectoryService.exists(storeId)) {
            return Result.failure(ApplicationError.conflict("Store", "error.detail.store-has-admin"));
        }
        if (registersStore && command.ruc() != null && !command.ruc().isBlank()
                && storeDirectoryService.isRucTaken(command.ruc())) {
            return Result.failure(ApplicationError.conflict("Store", "error.detail.store-ruc-taken"));
        }

        var role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        var user = new User(command.email(), hashingService.encode(command.password()), List.of(role));
        user.setName(command.name());
        user.setPhone(command.phone());
        if (!user.isCustomer()) {
            user.setStoreId(storeId);
        }
        if (user.isCustomer()) {
            user.setDni(command.dni().trim());
            customerDirectoryService.findByDni(user.getDni())
                    .ifPresent(link -> user.linkCustomer(link.customerId(), link.storeId()));
        }
        var verificationToken = generateToken();
        user.issueVerificationToken(verificationToken);
        userRepository.save(user);
        if (registersStore && command.businessName() != null && !command.businessName().isBlank()) {
            storeDirectoryService.register(storeId, command.ruc(), command.businessName(), command.category(),
                    command.phone(), command.email());
        }
        return userRepository.findByEmail(command.email())
                .<Result<User, ApplicationError>>map(savedUser -> {
                    eventPublisher.publishEvent(new UserSignedUpEvent(
                            savedUser.getId(), savedUser.getEmail(), RoleNameMapper.toClientRole(roleName), savedUser.getStoreId()));
                    notificationService.sendVerificationEmail(savedUser.getEmail(), savedUser.getName(),
                            verificationLink(verificationToken, savedUser.getEmail()));
                    return Result.success(savedUser);
                })
                .orElseGet(() -> Result.failure(ApplicationError.unexpected("sign-up", "error.detail.user-not-reloaded")));
    }

    @Override
    public Result<User, ApplicationError> handle(VerifyEmailCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty() || !user.get().confirmEmail(command.token().trim())) {
            return Result.failure(ApplicationError.validationError("token", "error.detail.invalid-token"));
        }
        return Result.success(userRepository.save(user.get()));
    }

    @Override
    public Result<Boolean, ApplicationError> handle(ResendVerificationCommand command) {
        userRepository.findByEmail(command.email())
                .filter(user -> !user.isEmailVerified())
                .ifPresent(user -> {
                    var verificationToken = generateToken();
                    user.issueVerificationToken(verificationToken);
                    var saved = userRepository.save(user);
                    notificationService.sendVerificationEmail(saved.getEmail(), saved.getName(),
                            verificationLink(verificationToken, saved.getEmail()));
                });
        return Result.success(true);
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
            return Result.failure(ApplicationError.serviceUnavailable("email", "error.detail.recovery-email-failed"));
        }
        return Result.success(saved);
    }

    @Override
    public Result<User, ApplicationError> handle(VerifyRecoveryCodeCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty() || !user.get().hasRecoveryCode(command.code().trim())) {
            return Result.failure(ApplicationError.validationError("code", "error.detail.invalid-code"));
        }
        if (user.get().isRecoveryCodeExpired(System.currentTimeMillis())) {
            return Result.failure(ApplicationError.businessRuleViolation("code-expired", "error.detail.code-expired"));
        }
        return Result.success(user.get());
    }

    @Override
    public Result<User, ApplicationError> handle(ResetPasswordCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty() || !user.get().hasRecoveryCode(command.code().trim())) {
            return Result.failure(ApplicationError.validationError("code", "error.detail.invalid-code"));
        }
        if (user.get().isRecoveryCodeExpired(System.currentTimeMillis())) {
            return Result.failure(ApplicationError.businessRuleViolation("code-expired", "error.detail.code-expired"));
        }
        user.get().resetPassword(hashingService.encode(command.newPassword()));
        user.get().revokeRefreshToken();
        user.get().markEmailVerified();
        var saved = userRepository.save(user.get());
        recordAction(saved, PASSWORD_RESET_ACTION, "Password reset for %s".formatted(saved.getEmail()));
        return Result.success(saved);
    }

    @Override
    public Result<AuthenticatedSession, ApplicationError> handle(SignInWithGoogleCommand command) {
        var email = googleIdentityService.verifiedEmail(command.accessToken());
        if (email.isEmpty()) {
            return Result.failure(ApplicationError.validationError("googleToken", "error.detail.invalid-google-token"));
        }
        var user = userRepository.findByEmail(email.get());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", email.get()));
        }
        user.get().markEmailVerified();
        return openSession(user.get(), true);
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
    public Result<AuthenticatedSession, ApplicationError> handle(ChangeEmailCommand command) {
        var user = userRepository.findByEmail(command.currentEmail());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.currentEmail()));
        }
        var newEmail = command.newEmail().trim();
        if (!newEmail.equalsIgnoreCase(command.currentEmail()) && userRepository.existsByEmail(newEmail)) {
            return Result.failure(ApplicationError.conflict("User", "error.detail.email-taken"));
        }
        var verificationToken = generateToken();
        if (!notificationService.sendVerificationEmail(newEmail, user.get().getName(),
                verificationLink(verificationToken, newEmail))) {
            return Result.failure(ApplicationError.serviceUnavailable("email", "error.detail.verification-email-failed"));
        }
        user.get().changeEmail(newEmail, verificationToken);
        var saved = userRepository.save(user.get());
        return Result.success(issueSession(saved));
    }

    @Override
    public Result<User, ApplicationError> handle(ChangePasswordCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.email()));
        }
        if (!hashingService.matches(command.currentPassword(), user.get().getPassword())) {
            return Result.failure(ApplicationError.validationError("currentPassword", "error.detail.wrong-password"));
        }
        user.get().setPassword(hashingService.encode(command.newPassword()));
        return Result.success(userRepository.save(user.get()));
    }

    private Roles resolveRoleName(String requestedRole) {

        return RoleNameMapper.fromClientRole(requestedRole, Role.getDefaultRole().getName());
    }

    private Result<AuthenticatedSession, ApplicationError> openSession(User user, boolean audited) {
        var linked = linkCustomerRecord(user);
        if (linked.isStoreAdmin() && storeDirectoryService.isInactive(linked.getStoreId())) {
            return Result.failure(ApplicationError.conflict("Store", "error.detail.store-inactive"));
        }
        if (audited) {
            recordAction(linked, USER_SIGNED_IN_ACTION, "User %s signed in".formatted(linked.getEmail()));
        }
        return Result.success(issueSession(linked));
    }

    private AuthenticatedSession issueSession(User user) {
        var refreshToken = generateToken();
        user.issueRefreshToken(hashOf(refreshToken), expiryAfter(refreshTokenTtl));
        var saved = userRepository.save(user);
        return new AuthenticatedSession(saved, tokenService.generateToken(saved.getEmail()), refreshToken);
    }

    private static String hashOf(String token) {
        try {
            var digest = MessageDigest.getInstance(REFRESH_TOKEN_DIGEST).digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean dniTaken(String dni) {
        return userRepository.findAll().stream().anyMatch(user -> dni.equals(user.getDni()));
    }

    private boolean systemAdminExists() {
        return userRepository.findAll().stream().anyMatch(User::isSystemAdmin);
    }

    private void recordAction(User user, String action, String details) {
        var role = user.getRoles().stream().findFirst().orElse(Role.getDefaultRole());
        eventPublisher.publishEvent(new AuditableActionRegisteredEvent(
                RoleNameMapper.toClientRole(role.getName()), action, details, user.getStoreId()));
    }

    private User linkCustomerRecord(User user) {
        if (!user.isCustomer() || user.getCustomerId() != null) {
            return user;
        }
        return customerDirectoryService.findByDni(user.getDni())
                .map(link -> {
                    user.linkCustomer(link.customerId(), link.storeId());
                    return userRepository.save(user);
                })
                .orElse(user);
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
