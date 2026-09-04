package com.vaultchecker.platform.customers.domain.model.queries;

/**
 * Query to get all customers. When {@code storeId} is provided, results are limited to that store.
 *
 * @param storeId optional store filter; {@code null} returns customers across all stores
 */
public record GetAllCustomersQuery(String storeId) {
}
