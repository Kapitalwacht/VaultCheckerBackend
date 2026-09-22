package com.vaultchecker.platform.credit.domain.model.valueobjects;

import java.math.BigDecimal;

public record AmortizationInstallment(int number, BigDecimal payment, BigDecimal interest,
                                      BigDecimal principal, BigDecimal remainingBalance) {
}
