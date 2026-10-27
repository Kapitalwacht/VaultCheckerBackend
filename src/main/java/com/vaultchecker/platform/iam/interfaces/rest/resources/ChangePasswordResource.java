package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ChangePasswordRequest", description = "Password change for the signed-in user")
public record ChangePasswordResource(
        @Schema(description = "Current password")
        @NotBlank @Size(max = 255)
        String currentPassword,
        @Schema(description = "New password (minimum 8 characters)", minLength = 8, maxLength = 255)
        @NotBlank @Size(min = 8, max = 255)
        String newPassword
) {
}
