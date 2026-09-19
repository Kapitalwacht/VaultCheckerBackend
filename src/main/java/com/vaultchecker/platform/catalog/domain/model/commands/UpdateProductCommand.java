package com.vaultchecker.platform.catalog.domain.model.commands;

import java.math.BigDecimal;

public record UpdateProductCommand(Long id, String productId, String storeId, String name, String category,
                                   String brand, String unit, BigDecimal cashPrice, BigDecimal listPrice,
                                   String paymentMode, String imageUrl, String state) {
}
