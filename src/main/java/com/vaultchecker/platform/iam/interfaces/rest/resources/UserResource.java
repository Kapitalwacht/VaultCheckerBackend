package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Resource representing an IAM user returned by the REST API.
 */
@Schema(name = "UserResponse", description = "User information response")
public record UserResource(
        @Schema(description = "User unique identifier", example = "1")
        Long id,

        @Schema(description = "User email", example = "owner@bodega.pe")
        String email,

        @Schema(description = "User assigned roles", example = "[\"ROLE_STORE_ADMIN\"]")
        List<String> roles
) {
}
