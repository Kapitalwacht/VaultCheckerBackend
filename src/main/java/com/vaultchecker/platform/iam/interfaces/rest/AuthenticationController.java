package com.vaultchecker.platform.iam.interfaces.rest;

import com.vaultchecker.platform.iam.application.commandservices.UserCommandService;
import com.vaultchecker.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.SignInResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.SignUpResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.UserResource;
import com.vaultchecker.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
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
                auth -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(auth.getLeft(), auth.getRight()),
                HttpStatus.OK
        );
    }

    @PostMapping("/sign-up")
    @Operation(summary = "User registration", description = "Creates a new user account with the provided credentials and role.")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var result = userCommandService.handle(signUpCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }
}
