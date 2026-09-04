package com.vaultchecker.platform.credit.domain.model.queries;

/**
 * Query to get all credit accounts. When {@code storeId} or {@code customerId} is provided, results
 * are filtered accordingly (a customer can only see their own account, US-22).
 *
 * @param storeId    optional store filter
 * @param customerId optional customer filter
 */
public record GetAllCreditAccountsQuery(String storeId, String customerId) {
}
