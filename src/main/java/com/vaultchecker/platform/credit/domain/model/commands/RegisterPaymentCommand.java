package com.vaultchecker.platform.credit.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterPaymentCommand(Long creditAccountId, String storeId, BigDecimal amount,
                                     LocalDate date, BigDecimal lateInterestDue, BigDecimal compensatoryInterestDue) {
}
