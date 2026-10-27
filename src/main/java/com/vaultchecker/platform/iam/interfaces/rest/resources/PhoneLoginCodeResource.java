package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PhoneLoginCodeResponse", description = "Phone sign-in code shown on screen (demo mode, no SMS provider)")
public record PhoneLoginCodeResource(
        @Schema(description = "One-time sign-in code", example = "482913")
        String code
) {
}
