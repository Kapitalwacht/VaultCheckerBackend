package com.vaultchecker.platform.catalog.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to update an existing product.
 */
public record UpdateProductCommand(Long id, String productId, String storeId, String name, String category,
                                   String unit, BigDecimal price, Integer stock, String state) {
}
