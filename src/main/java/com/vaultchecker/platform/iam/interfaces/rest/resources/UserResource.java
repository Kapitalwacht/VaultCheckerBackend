package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "UserResponse", description = "User information response")
public record UserResource(
        @Schema(description = "User unique identifier", example = "1")
        Long id,

        @Schema(description = "User email", example = "owner@bodega.pe")
        String email,

        @Schema(description = "Display name", example = "Don Pepe")
        String name,

        @Schema(description = "Contact phone", example = "+51 987654321")
        String phone,

        @Schema(description = "Store code the user belongs to", example = "ST-001")
        String storeId,

        @Schema(description = "Whether the email has been verified", example = "false")
        boolean emailVerified,

        @Schema(description = "User assigned roles", example = "[\"ROLE_STORE_ADMIN\"]")
        List<String> roles
) {
}
