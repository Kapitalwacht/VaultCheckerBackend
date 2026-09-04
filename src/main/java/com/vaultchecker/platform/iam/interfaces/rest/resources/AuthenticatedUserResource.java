package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource returned after successful authentication. Field names match what the VaultChecker
 * frontend expects ({@code id}, {@code userId}, {@code email}, {@code role}, {@code token}).
 */
@Schema(name = "AuthenticatedUserResponse", description = "Authenticated user information with JWT token")
public record AuthenticatedUserResource(
        @Schema(description = "User unique identifier", example = "1")
        Long id,

        @Schema(description = "User unique identifier (alias used by the frontend)", example = "1")
        Long userId,

        @Schema(description = "User email", example = "owner@bodega.pe")
        String email,

        @Schema(description = "Primary role assigned to the user", example = "ROLE_STORE_ADMIN")
        String role,

        @Schema(description = "JWT Bearer token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
