package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "SignUpRequest", description = "User sign-up request with credentials")
public record SignUpResource(
        @Schema(description = "User email", example = "owner@bodega.pe", maxLength = 120)
        @NotBlank @Email @Size(max = 120)
        String email,

        @Schema(description = "User password (minimum 8 characters)", example = "SecurePass123!", minLength = 8, maxLength = 255)
        @NotBlank @Size(min = 8, max = 255)
        String password,

        @Schema(description = "Role to assign", example = "ROLE_STORE_ADMIN",
                allowableValues = {"ROLE_SYSTEM_ADMIN", "ROLE_STORE_ADMIN", "ROLE_CUSTOMER"}, nullable = true)
        String role,

        @Schema(description = "Display name of the account owner", example = "Don Pepe", nullable = true)
        String name,

        @Schema(description = "Contact phone", example = "+51 987654321", nullable = true)
        String phone,

        @Schema(description = "Store code the user belongs to", example = "ST-001", nullable = true)
        String storeId
) {
}
