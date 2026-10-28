package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "GoogleSignInRequest", description = "Google sign-in with an OAuth access token issued to the frontend")
public record GoogleSignInResource(
        @Schema(description = "Google OAuth access token")
        @NotBlank @Size(max = 4096)
        String accessToken
) {
}
