package com.vaultchecker.platform.credit.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resource representing a registered payment, including the allocation order breakdown.
 */
public record PaymentResource(Long id, Long creditAccountId, String storeId, BigDecimal amount, LocalDate date,
                              BigDecimal appliedToLateInterest, BigDecimal appliedToCompensatoryInterest,
                              BigDecimal appliedToPrincipal) {
}
