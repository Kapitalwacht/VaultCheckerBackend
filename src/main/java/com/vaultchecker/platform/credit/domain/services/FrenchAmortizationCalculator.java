package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.AmortizationInstallment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static com.vaultchecker.platform.credit.domain.services.FinanceConstants.*;

public final class FrenchAmortizationCalculator {

    private static final MathContext MC = new MathContext(20);

    private FrenchAmortizationCalculator() {
    }

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
