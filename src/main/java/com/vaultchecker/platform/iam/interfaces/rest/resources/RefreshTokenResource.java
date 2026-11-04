package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "RefreshTokenRequest", description = "Refresh token issued at sign-in")
public record RefreshTokenResource(
        @Schema(description = "Refresh token of the current session")
        @NotBlank
        String refreshToken
) {
}
