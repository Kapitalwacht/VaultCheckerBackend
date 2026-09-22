package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.PaymentAllocation;

import java.math.BigDecimal;

public final class PaymentAllocationCalculator {

    private PaymentAllocationCalculator() {
    }

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
