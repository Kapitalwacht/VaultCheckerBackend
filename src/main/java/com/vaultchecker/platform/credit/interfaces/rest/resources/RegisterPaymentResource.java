package com.vaultchecker.platform.credit.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterPaymentResource(
        @NotNull Long creditAccountId,
        String storeId,
        @NotNull BigDecimal amount,
        LocalDate date,
        BigDecimal lateInterestDue,
        BigDecimal compensatoryInterestDue
) {
}
