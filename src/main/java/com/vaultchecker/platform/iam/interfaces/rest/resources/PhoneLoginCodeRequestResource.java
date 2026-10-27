package com.vaultchecker.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "PhoneLoginCodeRequest", description = "Request for a phone sign-in code")
public record PhoneLoginCodeRequestResource(
        @Schema(description = "Phone registered in the account", example = "+51 987654321")
        @NotBlank @Size(max = 30)
        String phone
) {
}
