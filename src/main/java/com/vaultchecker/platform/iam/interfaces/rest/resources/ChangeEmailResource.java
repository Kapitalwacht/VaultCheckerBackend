package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ChangeEmailRequest", description = "Email change for the signed-in user")
public record ChangeEmailResource(
        @Schema(description = "New email", example = "owner@bodega.pe")
        @NotBlank @Email @Size(max = 120)
        String email
) {
}
