package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RecoveryCodeVerificationRequest", description = "Check of a password recovery code before choosing a new password")
public record RecoveryCodeVerificationResource(
        @Schema(description = "Email of the account to recover", example = "owner@bodega.pe")
        @NotBlank @Email @Size(max = 120)
        String email,
        @Schema(description = "Recovery code received by email", example = "482913")
        @NotBlank @Size(max = 10)
        String code
) {
}
