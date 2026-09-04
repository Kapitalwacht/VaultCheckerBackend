package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.RateType;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.vaultchecker.platform.credit.domain.services.FinanceConstants.*;

/**
 * Pure domain calculator for the interest concepts in the project brief (Épica 6). All results honour
 * the mandatory conventions in {@link FinanceConstants}: 360-day base, 30-day month, amounts rounded
 * to 2 decimals and rates carried with at least 7 decimals.
 *
 * <p>The reference rate throughout is an <b>effective annual rate</b>. Use
 * {@link #toEffectiveAnnual(BigDecimal, RateType, int)} to normalise a pactada nominal or effective
 * rate before feeding it to the other methods.</p>
 */
public final class InterestCalculator {

    private static final MathContext MC = new MathContext(20);

    private InterestCalculator() {
    }

    /**
     * Normalises a pactada rate to an effective annual rate.
     *
     * @param rate            the pactada rate value (e.g. 0.24 for 24%)
     * @param type            whether {@code rate} is NOMINAL or EFFECTIVE
     * @param compoundingsPerYear number of compounding periods per year for a nominal rate
     *                            (e.g. 12 for a monthly-compounded nominal annual rate); ignored for
     *                            an effective rate
     * @return the equivalent effective annual rate, scaled to {@link FinanceConstants#RATE_SCALE}
     */
    public static BigDecimal toEffectiveAnnual(BigDecimal rate, RateType type, int compoundingsPerYear) {
        if (type == RateType.EFFECTIVE) {
            return scaleRate(rate);
        }
        // Effective annual = (1 + j/m)^m - 1
        double j = rate.doubleValue();
        double effective = Math.pow(1.0 + j / compoundingsPerYear, compoundingsPerYear) - 1.0;
        return scaleRate(BigDecimal.valueOf(effective));
    }

    /**
     * Converts an effective annual rate to the equivalent effective rate for a period of {@code days}
     * (using the 360-day base): {@code (1 + i)^(days/360) - 1}.
     */
    public static BigDecimal effectiveRateForDays(BigDecimal effectiveAnnualRate, int days) {
        double i = effectiveAnnualRate.doubleValue();
        double periodic = Math.pow(1.0 + i, (double) days / DAY_COUNT_BASE) - 1.0;
        return scaleRate(BigDecimal.valueOf(periodic));
    }

    /**
     * Converts an effective annual rate to the equivalent effective monthly rate (30-day month):
     * {@code (1 + i)^(30/360) - 1}.
     */
    public static BigDecimal effectiveMonthlyRate(BigDecimal effectiveAnnualRate) {
        return effectiveRateForDays(effectiveAnnualRate, COMMERCIAL_MONTH_DAYS);
    }

    /**
     * Compensatory interest accrued over {@code days} on {@code principal} at the given effective
     * annual rate (US-16). Base 360.
     *
     * @return the interest amount rounded to {@link FinanceConstants#MONEY_SCALE}
     */
    public static BigDecimal compensatoryInterest(BigDecimal principal, BigDecimal effectiveAnnualRate, int days) {
        BigDecimal periodicRate = effectiveRateForDays(effectiveAnnualRate, days);
        return scaleMoney(principal.multiply(periodicRate, MC));
    }

    /**
     * Moratory (late) interest for {@code daysLate} days on {@code overdueAmount} at the pactada
     * moratory effective annual rate (US-18). Base 360.
     */
    public static BigDecimal moratoryInterest(BigDecimal overdueAmount, BigDecimal moratoryEffectiveAnnualRate, int daysLate) {
        if (daysLate <= 0) {
            return scaleMoney(BigDecimal.ZERO);
        }
        BigDecimal periodicRate = effectiveRateForDays(moratoryEffectiveAnnualRate, daysLate);
        return scaleMoney(overdueAmount.multiply(periodicRate, MC));
    }

    /**
     * Capitalises {@code principal} over the {@code graceDays} dead days before the first installment
     * (US-17): {@code principal * (1 + i)^(graceDays/360)}. When there are no grace days the principal
     * is returned unchanged.
     */
    public static BigDecimal capitalizeForGrace(BigDecimal principal, BigDecimal effectiveAnnualRate, int graceDays) {
        if (graceDays <= 0) {
            return scaleMoney(principal);
        }
        double factor = Math.pow(1.0 + effectiveAnnualRate.doubleValue(), (double) graceDays / DAY_COUNT_BASE);
        return scaleMoney(principal.multiply(BigDecimal.valueOf(factor), MC));
    }

    /** Rounds a monetary amount to the mandatory 2 decimals. */
    public static BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }

    /** Scales a rate to the mandatory minimum of 7 decimals. */
    public static BigDecimal scaleRate(BigDecimal value) {
        return value.setScale(RATE_SCALE, RATE_ROUNDING);
    }
}
