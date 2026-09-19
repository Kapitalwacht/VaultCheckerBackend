package com.vaultchecker.platform.credit.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentPaymentResource(Long id, String paymentId, String storeId, String customerId,
                                         String purchaseId, Integer period, LocalDate scheduledDate,
                                         LocalDate paidDate, BigDecimal installment, BigDecimal lateFee,
                                         BigDecimal interest, BigDecimal principal, BigDecimal total, String state) {
}
