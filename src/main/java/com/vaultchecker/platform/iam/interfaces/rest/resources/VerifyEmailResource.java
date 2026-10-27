package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "VerifyEmailRequest", description = "Email verification request")
public record VerifyEmailResource(
        @Schema(description = "Email being verified", example = "owner@bodega.pe")
        @NotBlank @Email @Size(max = 120)
        String email,
        @Schema(description = "Token received in the verification link")
        @NotBlank @Size(max = 60)
        String token
) {
}
