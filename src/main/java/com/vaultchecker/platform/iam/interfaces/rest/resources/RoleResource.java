package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource that represents an IAM role exposed by the REST API.
 */
@Schema(name = "RoleResponse", description = "Role information response")
public record RoleResource(
        @Schema(description = "Role unique identifier", example = "1")
        Long id,

        @Schema(description = "Role name", example = "ROLE_STORE_ADMIN",
                allowableValues = {"ROLE_SYSTEM_ADMIN", "ROLE_STORE_ADMIN", "ROLE_CUSTOMER"})
        String name
) {
}
