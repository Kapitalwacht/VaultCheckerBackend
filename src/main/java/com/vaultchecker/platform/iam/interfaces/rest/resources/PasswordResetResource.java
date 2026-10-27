package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "PasswordResetRequest", description = "Password reset with a recovery code")
public record PasswordResetResource(
        @Schema(description = "Email of the account to recover", example = "owner@bodega.pe")
        @NotBlank @Email @Size(max = 120)
        String email,
        @Schema(description = "Recovery code received by email", example = "482913")
        @NotBlank @Size(max = 10)
        String code,
        @Schema(description = "New password (minimum 8 characters)", minLength = 8, maxLength = 255)
        @NotBlank @Size(min = 8, max = 255)
        String newPassword
) {
}
