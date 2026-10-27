package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UpdateProfileRequest", description = "Profile update for the signed-in user")
public record UpdateProfileResource(
        @Schema(description = "Display name", example = "Don Pepe")
        @NotBlank @Size(max = 120)
        String name,
        @Schema(description = "Contact phone", example = "+51 987654321")
        @Size(max = 30)
        String phone
) {
}
