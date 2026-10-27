package com.vaultchecker.platform.iam.interfaces.rest;

import com.vaultchecker.platform.iam.application.commandservices.UserCommandService;
import com.vaultchecker.platform.iam.application.queryservices.UserQueryService;
import com.vaultchecker.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.vaultchecker.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.vaultchecker.platform.iam.interfaces.rest.resources.ChangeEmailResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.ChangePasswordResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.UpdateProfileResource;
import com.vaultchecker.platform.iam.interfaces.rest.resources.UserResource;
import com.vaultchecker.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.ChangeEmailCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.ChangePasswordCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import com.vaultchecker.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.vaultchecker.platform.shared.interfaces.rest.resources.MessageResource;
import com.vaultchecker.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User management endpoints")
public class UsersController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @GetMapping
    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var users = userQueryService.handle(new GetAllUsersQuery());
        var userResources = users.stream().map(UserResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(userResources);
    }

    @GetMapping(value = "/{userId}")
    @Operation(summary = "Get user by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        var user = userQueryService.handle(new GetUserByIdQuery(userId));
        return user
                .map(value -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Update the signed-in user's profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails principal,
                                           @Valid @RequestBody UpdateProfileResource resource) {
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(principal.getUsername(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                userCommandService.handle(command),
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PutMapping("/me/email")
    @Operation(summary = "Change the signed-in user's email",
            description = "Emails a verification link to the new address and returns a new JWT token for it.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> changeEmail(@AuthenticationPrincipal UserDetails principal,
                                         @Valid @RequestBody ChangeEmailResource resource) {
        var command = ChangeEmailCommandFromResourceAssembler.toCommandFromResource(principal.getUsername(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                userCommandService.handle(command),
                auth -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(auth.getLeft(), auth.getRight()),
                HttpStatus.OK
        );
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change the signed-in user's password", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails principal,
                                            @Valid @RequestBody ChangePasswordResource resource) {
        var command = ChangePasswordCommandFromResourceAssembler.toCommandFromResource(principal.getUsername(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                userCommandService.handle(command),
                user -> new MessageResource("Password updated"),
                HttpStatus.OK
        );
    }
}
