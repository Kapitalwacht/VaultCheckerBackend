package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "PhoneSignInRequest", description = "Phone sign-in with a one-time code")
public record PhoneSignInResource(
        @Schema(description = "Phone registered in the account", example = "+51 987654321")
        @NotBlank @Size(max = 30)
        String phone,
        @Schema(description = "Sign-in code", example = "482913")
        @NotBlank @Size(max = 10)
        String code
) {
}
