package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ResendVerificationRequest", description = "Request to send the verification email again")
public record ResendVerificationResource(
        @Schema(description = "Email of the account to verify", example = "owner@bodega.pe")
        @NotBlank @Email @Size(max = 120)
        String email
) {
}
