package com.vaultchecker.platform.catalog.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Resource representing a product returned by the REST API. Field names match the frontend.
 */
public record ProductResource(Long id, String productId, String storeId, String name, String category,
                              String unit, BigDecimal price, Integer stock, String state) {
}
