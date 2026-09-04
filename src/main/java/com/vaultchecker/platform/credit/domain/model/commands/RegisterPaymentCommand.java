package com.vaultchecker.platform.credit.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to register a payment against a credit account (US-19, US-20).
 */
public record RegisterPaymentCommand(Long creditAccountId, String storeId, BigDecimal amount,
                                     LocalDate date, BigDecimal lateInterestDue, BigDecimal compensatoryInterestDue) {
}
