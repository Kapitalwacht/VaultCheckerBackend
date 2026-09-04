package com.vaultchecker.platform.catalog.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to register a new product (US-06).
 */
public record CreateProductCommand(String productId, String storeId, String name, String category,
                                   String unit, BigDecimal price, Integer stock) {
}
