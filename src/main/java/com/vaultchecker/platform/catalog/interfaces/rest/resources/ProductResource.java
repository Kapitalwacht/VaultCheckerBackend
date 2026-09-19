package com.vaultchecker.platform.catalog.interfaces.rest.resources;

import java.math.BigDecimal;

public record ProductResource(Long id, String productId, String storeId, String name, String category, String brand,
                              String unit, BigDecimal cashPrice, BigDecimal listPrice, String paymentMode,
                              String imageUrl, String state) {
}
