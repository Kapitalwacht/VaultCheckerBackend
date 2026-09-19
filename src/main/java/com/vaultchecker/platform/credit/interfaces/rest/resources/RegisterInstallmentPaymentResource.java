package com.vaultchecker.platform.credit.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterInstallmentPaymentResource(
        @Size(max = 60) String paymentId,
        @Size(max = 60) String storeId,
        @Size(max = 60) String customerId,
        @NotBlank @Size(max = 60) String purchaseId,
        Integer period,
        LocalDate scheduledDate,
        LocalDate paidDate,
        BigDecimal installment,
        BigDecimal lateFee,
        BigDecimal interest,
        BigDecimal principal,
        BigDecimal total,
        @Size(max = 20) String state
) {
}
