package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Pure helpers for the credit cycle: resolving a customer's effective annual rate from their pactada
 * rate, and locating the grace-period end (the first payment-day on/after a purchase, respecting the
 * cutoff cycle). Shared by the purchase and statement flows so both compute dates and rates identically.
 */
public final class CreditCycle {

    private CreditCycle() {
    }

    /**
     * Converts a pactada rate (nominal or effective) into an effective annual rate. For a nominal rate
     * the number of compounding periods per year is {@code periodDays / capitalizationDays}
     * (e.g. 360/30 = 12).
     */
    public static BigDecimal effectiveAnnualRate(String rateType, BigDecimal rateValue,
                                                 Integer periodDays, Integer capitalizationDays) {
        if (rateValue == null) {
            return BigDecimal.ZERO;
        }
        var type = "nominal".equalsIgnoreCase(rateType) ? RateType.NOMINAL : RateType.EFFECTIVE;
        int period = periodDays != null ? periodDays : FinanceConstants.DAY_COUNT_BASE;
        int cap = capitalizationDays != null && capitalizationDays > 0
                ? capitalizationDays : FinanceConstants.COMMERCIAL_MONTH_DAYS;
        int compoundingsPerYear = Math.max(1, period / cap);
        return InterestCalculator.toEffectiveAnnual(rateValue, type, compoundingsPerYear);
    }

    /**
     * First payment-day on/after {@code purchaseDate}, respecting the cutoff cycle. This marks the end of
     * the grace period: a purchase on or before the cutoff pays in the current cycle, otherwise next month.
     */
    public static LocalDate graceEndDate(LocalDate purchaseDate, int cutoffDay, int paymentDay) {
        var base = purchaseDate.getDayOfMonth() <= cutoffDay ? purchaseDate : purchaseDate.plusMonths(1);
        var anchor = atDay(base, paymentDay);
        if (!anchor.isAfter(purchaseDate)) {
            anchor = atDay(base.plusMonths(1), paymentDay);
        }
        return anchor;
    }

    /** Number of grace days between the purchase and the grace-period end (never negative). */
    public static int graceDays(LocalDate purchaseDate, int cutoffDay, int paymentDay) {
        return (int) Math.max(0, ChronoUnit.DAYS.between(purchaseDate, graceEndDate(purchaseDate, cutoffDay, paymentDay)));
    }

    private static LocalDate atDay(LocalDate month, int day) {
        return month.withDayOfMonth(Math.min(day, month.lengthOfMonth()));
    }
}
