package com.vaultchecker.platform.iam.interfaces.rest;

import com.vaultchecker.platform.iam.application.queryservices.UserQueryService;
import com.vaultchecker.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.vaultchecker.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.vaultchecker.platform.iam.interfaces.rest.resources.UserResource;
import com.vaultchecker.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes IAM user resources.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User management endpoints")
public class UsersController {
    private final UserQueryService userQueryService;

    public UsersController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
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
}
