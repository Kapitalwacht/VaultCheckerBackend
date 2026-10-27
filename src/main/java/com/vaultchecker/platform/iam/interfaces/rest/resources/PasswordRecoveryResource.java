package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "PasswordRecoveryRequest", description = "Request to email a password recovery code")
public record PasswordRecoveryResource(
        @Schema(description = "Email of the account to recover", example = "owner@bodega.pe")
        @NotBlank @Email @Size(max = 120)
        String email
) {
}
