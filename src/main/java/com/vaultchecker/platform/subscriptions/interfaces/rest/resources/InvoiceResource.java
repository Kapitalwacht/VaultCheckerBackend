package com.vaultchecker.platform.subscriptions.interfaces.rest.resources;

public record InvoiceResource(Long id, String invoiceId, String storeId, String planId,
                              double amount, String currency, String status, String date) {
}
