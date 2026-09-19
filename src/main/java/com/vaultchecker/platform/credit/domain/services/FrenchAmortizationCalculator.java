package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.AmortizationInstallment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static com.vaultchecker.platform.credit.domain.services.FinanceConstants.*;

/**
 * Builds a payment schedule using the <b>French method (vencido)</b> with equal installments, as
 * required for the installment ("meses") modality. Commercial months of 30 days are used and
 * the principal is capitalised for the grace period before the installment is computed.
 *
 * <p>Fixed installment: {@code A = P * r / (1 - (1 + r)^-n)} where {@code r} is the effective monthly
 * rate (derived from the effective annual rate over a 30-day month) and {@code n} the number of
 * months.</p>
 */
public final class FrenchAmortizationCalculator {

    private static final MathContext MC = new MathContext(20);

    private FrenchAmortizationCalculator() {
    }

    /**
     * Computes the fixed installment (cuota) after capitalising the grace period.
     *
     * @param principal          the purchase amount financed
     * @param effectiveAnnualRate the customer's effective annual rate
     * @param months             number of monthly installments (must be >= 1)
     * @param graceDays          dead days between the purchase and the first installment
     * @return the fixed installment rounded to 2 decimals
     */
    public static BigDecimal fixedInstallment(BigDecimal principal, BigDecimal effectiveAnnualRate,
                                              int months, int graceDays) {
        if (months < 1) {
            throw new IllegalArgumentException("months must be >= 1");
        }
        BigDecimal capitalized = InterestCalculator.capitalizeForGrace(principal, effectiveAnnualRate, graceDays);
        double r = InterestCalculator.effectiveMonthlyRate(effectiveAnnualRate).doubleValue();
        double payment;
        if (r == 0.0) {
            payment = capitalized.doubleValue() / months;
        } else {
            double factor = r / (1.0 - Math.pow(1.0 + r, -months));
            payment = capitalized.doubleValue() * factor;
        }
        return InterestCalculator.scaleMoney(BigDecimal.valueOf(payment));
    }

    /**
     * Builds the full amortization schedule (cronograma) with the interest/principal breakdown per
     * installment. The last installment absorbs any rounding residual so the balance closes at zero.
     */
    public static List<AmortizationInstallment> schedule(BigDecimal principal, BigDecimal effectiveAnnualRate,
                                                         int months, int graceDays) {
        BigDecimal capitalized = InterestCalculator.capitalizeForGrace(principal, effectiveAnnualRate, graceDays);
        BigDecimal monthlyRate = InterestCalculator.effectiveMonthlyRate(effectiveAnnualRate);
        BigDecimal installment = fixedInstallment(principal, effectiveAnnualRate, months, graceDays);

        List<AmortizationInstallment> rows = new ArrayList<>();
        BigDecimal balance = capitalized;
        for (int n = 1; n <= months; n++) {
            BigDecimal interest = InterestCalculator.scaleMoney(balance.multiply(monthlyRate, MC));
            BigDecimal principalPart;
            BigDecimal payment;
            if (n == months) {
                // Final installment: clear the remaining balance exactly.
                principalPart = balance;
                payment = InterestCalculator.scaleMoney(balance.add(interest));
            } else {
                principalPart = InterestCalculator.scaleMoney(installment.subtract(interest));
                payment = installment;
            }
            balance = InterestCalculator.scaleMoney(balance.subtract(principalPart));
            rows.add(new AmortizationInstallment(n, payment, interest, principalPart, balance.max(BigDecimal.ZERO)));
        }
        return rows;
    }
}
