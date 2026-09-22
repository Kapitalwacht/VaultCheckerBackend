package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

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

        @Schema(description = "Display name", example = "Don Pepe")
        String name,

        @Schema(description = "Contact phone", example = "+51 987654321")
        String phone,

        @Schema(description = "Store code the user belongs to", example = "ST-001")
        String storeId,

        @Schema(description = "Customer code when the user is a customer", example = "CU-001")
        String customerId,

        @Schema(description = "JWT Bearer token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
