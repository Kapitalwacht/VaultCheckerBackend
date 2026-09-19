package com.vaultchecker.platform.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record ChangePlanResource(
        @NotBlank String storeId,
        @NotBlank String planId
) {
}
