package com.vaultchecker.platform.credit.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resource received to register a payment against a credit account.
 * {@code lateInterestDue} and {@code compensatoryInterestDue} feed the payment prelación (US-19).
 */
public record RegisterPaymentResource(
        @NotNull Long creditAccountId,
        String storeId,
        @NotNull BigDecimal amount,
        LocalDate date,
        BigDecimal lateInterestDue,
        BigDecimal compensatoryInterestDue
) {
}
