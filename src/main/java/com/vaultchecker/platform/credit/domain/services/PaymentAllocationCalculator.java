package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.PaymentAllocation;

import java.math.BigDecimal;

/**
 * Applies the payment allocation order: an incoming payment is imputed in strict order to
 * (1) moratory/late interest, (2) compensatory interest, and finally (3) principal (capital).
 */
public final class PaymentAllocationCalculator {

    private PaymentAllocationCalculator() {
    }

    /**
     * Allocates {@code payment} across the outstanding buckets in allocation order order.
     *
     * @param payment              the amount being paid
     * @param lateInterestDue      outstanding moratory interest
     * @param compensatoryDue      outstanding compensatory interest
     * @param principalDue         outstanding principal
     * @return the breakdown of how the payment was applied, with any leftover in {@code unapplied}
     */
    public static PaymentAllocation allocate(BigDecimal payment, BigDecimal lateInterestDue,
                                             BigDecimal compensatoryDue, BigDecimal principalDue) {
        BigDecimal remaining = InterestCalculator.scaleMoney(payment);

        BigDecimal toLate = min(remaining, InterestCalculator.scaleMoney(lateInterestDue));
        remaining = remaining.subtract(toLate);

        BigDecimal toCompensatory = min(remaining, InterestCalculator.scaleMoney(compensatoryDue));
        remaining = remaining.subtract(toCompensatory);

        BigDecimal toPrincipal = min(remaining, InterestCalculator.scaleMoney(principalDue));
        remaining = remaining.subtract(toPrincipal);

        return new PaymentAllocation(
                InterestCalculator.scaleMoney(toLate),
                InterestCalculator.scaleMoney(toCompensatory),
                InterestCalculator.scaleMoney(toPrincipal),
                InterestCalculator.scaleMoney(remaining));
    }

    private static BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a.signum() <= 0) return BigDecimal.ZERO;
        if (b.signum() <= 0) return BigDecimal.ZERO;
        return a.min(b);
    }
}
