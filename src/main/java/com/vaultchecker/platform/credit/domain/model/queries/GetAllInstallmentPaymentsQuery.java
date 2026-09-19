package com.vaultchecker.platform.credit.domain.model.queries;

public record GetAllInstallmentPaymentsQuery(String storeId, String customerId, String purchaseId) {
}
