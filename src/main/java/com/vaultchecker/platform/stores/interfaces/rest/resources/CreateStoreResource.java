package com.vaultchecker.platform.stores.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStoreResource(
        @Size(max = 60) String storeId,
        @Size(max = 20) String ruc,
        @NotBlank @Size(max = 150) String businessName,
        @Size(max = 80) String category,
        @Size(max = 200) String address,
        @Size(max = 30) String phone,
        @Size(max = 120) String email,
        @Size(max = 500) String description
) {
}
