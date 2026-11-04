package com.vaultchecker.platform.iam.interfaces.rest;

import com.vaultchecker.platform.iam.application.commandservices.UserCommandService;
import com.vaultchecker.platform.iam.interfaces.rest.resources.GoogleSignInResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.PasswordRecoveryResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.PasswordResetResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.RecoveryCodeVerificationResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.RefreshTokenResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.ResendVerificationResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.SignInResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.SignUpResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.VerifyEmailResource;
import com.vaultchecker.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.RefreshSessionCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.RequestPasswordRecoveryCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.ResendVerificationCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.ResetPasswordCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.SignInWithGoogleCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.SignOutCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.VerifyEmailCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.VerifyRecoveryCodeCommandFromResourceAssembler;
import com.vaultchecker.platform.shared.interfaces.rest.resources.MessageResource;
import com.vaultchecker.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthenticationController {
    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "User sign-in", description = "Authenticates a user and returns a JWT token.")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var result = userCommandService.handle(signInCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AuthenticatedUserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PostMapping("/sign-up")
    @Operation(summary = "User registration", description = "Creates a new user account with the provided credentials and role, and emails a verification link.")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var result = userCommandService.handle(signUpCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Marks the email as verified using the token from the verification link.")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailResource resource) {
        var result = userCommandService.handle(VerifyEmailCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email", description = "Sends a new verification link when the account is not verified yet.")
    public ResponseEntity<?> resendVerification(@Valid @RequestBody ResendVerificationResource resource) {
        var result = userCommandService.handle(ResendVerificationCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                sent -> new MessageResource("Verification email sent"),
                HttpStatus.OK
        );
    }

    @PostMapping("/password-recovery")
    @Operation(summary = "Request password recovery", description = "Emails a one-time recovery code valid for 15 minutes.")
    public ResponseEntity<?> requestPasswordRecovery(@Valid @RequestBody PasswordRecoveryResource resource) {
        var result = userCommandService.handle(RequestPasswordRecoveryCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                user -> new MessageResource("Recovery code sent"),
                HttpStatus.OK
        );
    }

    @PostMapping("/password-recovery/verify")
    @Operation(summary = "Verify recovery code", description = "Checks that the emailed recovery code is valid and not expired, without changing the password.")
    public ResponseEntity<?> verifyRecoveryCode(@Valid @RequestBody RecoveryCodeVerificationResource resource) {
        var result = userCommandService.handle(VerifyRecoveryCodeCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                user -> new MessageResource("Recovery code verified"),
                HttpStatus.OK
        );
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Reset password", description = "Sets a new password using the emailed recovery code.")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetResource resource) {
        var result = userCommandService.handle(ResetPasswordCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                user -> new MessageResource("Password updated"),
                HttpStatus.OK
        );
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh session", description = "Exchanges a valid refresh token for a new access token and rotates the refresh token.")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenResource resource) {
        var result = userCommandService.handle(RefreshSessionCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AuthenticatedUserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PostMapping("/sign-out")
    @Operation(summary = "Sign out", description = "Revokes the refresh token of the session.")
    public ResponseEntity<?> signOut(@Valid @RequestBody RefreshTokenResource resource) {
        var result = userCommandService.handle(SignOutCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                signedOut -> new MessageResource("Signed out"),
                HttpStatus.OK
        );
    }

    @PostMapping("/google-sign-in")
    @Operation(summary = "Google sign-in", description = "Verifies a Google OAuth access token issued to the VaultChecker client and returns a JWT token for the account with that email.")
    public ResponseEntity<?> signInWithGoogle(@Valid @RequestBody GoogleSignInResource resource) {
        var result = userCommandService.handle(SignInWithGoogleCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AuthenticatedUserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }
}
