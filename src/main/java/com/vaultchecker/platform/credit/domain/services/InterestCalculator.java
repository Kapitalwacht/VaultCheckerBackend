package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.RateType;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.vaultchecker.platform.credit.domain.services.FinanceConstants.*;

public final class InterestCalculator {

    private static final MathContext MC = new MathContext(20);

    private InterestCalculator() {
    }

    public static BigDecimal toEffectiveAnnual(BigDecimal rate, RateType type, int compoundingsPerYear) {
        if (type == RateType.EFFECTIVE) {
            return scaleRate(rate);
        }

        double j = rate.doubleValue();
        double effective = Math.pow(1.0 + j / compoundingsPerYear, compoundingsPerYear) - 1.0;
        return scaleRate(BigDecimal.valueOf(effective));
    }

    public static BigDecimal effectiveRateForDays(BigDecimal effectiveAnnualRate, int days) {
        double i = effectiveAnnualRate.doubleValue();
        double periodic = Math.pow(1.0 + i, (double) days / DAY_COUNT_BASE) - 1.0;
        return scaleRate(BigDecimal.valueOf(periodic));
    }

    public static BigDecimal effectiveMonthlyRate(BigDecimal effectiveAnnualRate) {
        return effectiveRateForDays(effectiveAnnualRate, COMMERCIAL_MONTH_DAYS);
    }

    public static BigDecimal compensatoryInterest(BigDecimal principal, BigDecimal effectiveAnnualRate, int days) {
        BigDecimal periodicRate = effectiveRateForDays(effectiveAnnualRate, days);
        return scaleMoney(principal.multiply(periodicRate, MC));
    }

    public static BigDecimal moratoryInterest(BigDecimal overdueAmount, BigDecimal moratoryEffectiveAnnualRate, int daysLate) {
        if (daysLate <= 0) {
            return scaleMoney(BigDecimal.ZERO);
        }
        BigDecimal periodicRate = effectiveRateForDays(moratoryEffectiveAnnualRate, daysLate);
        return scaleMoney(overdueAmount.multiply(periodicRate, MC));
    }

    public static BigDecimal capitalizeForGrace(BigDecimal principal, BigDecimal effectiveAnnualRate, int graceDays) {
        if (graceDays <= 0) {
            return scaleMoney(principal);
        }
        double factor = Math.pow(1.0 + effectiveAnnualRate.doubleValue(), (double) graceDays / DAY_COUNT_BASE);
        return scaleMoney(principal.multiply(BigDecimal.valueOf(factor), MC));
    }

    public static BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }

    public static BigDecimal scaleRate(BigDecimal value) {
        return value.setScale(RATE_SCALE, RATE_ROUNDING);
    }
}
