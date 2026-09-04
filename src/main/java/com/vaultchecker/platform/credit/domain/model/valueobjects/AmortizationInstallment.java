package com.vaultchecker.platform.credit.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * A single row of a French-method amortization schedule.
 *
 * @param number           installment number (1-based)
 * @param payment          the fixed installment amount (cuota)
 * @param interest         interest portion of the installment
 * @param principal        principal (capital) portion of the installment
 * @param remainingBalance outstanding balance after paying this installment
 */
public record AmortizationInstallment(int number, BigDecimal payment, BigDecimal interest,
                                      BigDecimal principal, BigDecimal remainingBalance) {
}
