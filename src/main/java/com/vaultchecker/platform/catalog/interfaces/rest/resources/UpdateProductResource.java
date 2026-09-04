package com.vaultchecker.platform.catalog.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Resource received to update an existing product.
 */
public record UpdateProductResource(
        @Size(max = 60) String productId,
        @Size(max = 60) String storeId,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 80) String category,
        @Size(max = 30) String unit,
        BigDecimal price,
        Integer stock,
        @Size(max = 20) String state
) {
}
