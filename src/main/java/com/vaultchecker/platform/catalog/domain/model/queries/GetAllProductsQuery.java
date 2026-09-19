package com.vaultchecker.platform.catalog.domain.model.queries;

/**
 * Query to get all products. When {@code storeId} is provided, results are limited to that store
 * (tenant isolation).
 *
 * @param storeId optional store filter; {@code null} returns products across all stores
 */
public record GetAllProductsQuery(String storeId) {
}
