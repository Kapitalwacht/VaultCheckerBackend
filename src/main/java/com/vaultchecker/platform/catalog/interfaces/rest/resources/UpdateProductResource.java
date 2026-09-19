package com.vaultchecker.platform.catalog.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductResource(
        @Size(max = 60) String productId,
        @Size(max = 60) String storeId,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 80) String category,
        @Size(max = 80) String brand,
        @Size(max = 30) String unit,
        BigDecimal cashPrice,
        BigDecimal listPrice,
        @Size(max = 20) String paymentMode,
        @Size(max = 500) String imageUrl,
        @Size(max = 20) String state
) {
}
