package com.vaultchecker.platform.credit.domain.services;

import com.vaultchecker.platform.credit.domain.model.valueobjects.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class CreditCycle {

    private CreditCycle() {
    }

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

    public static LocalDate graceEndDate(LocalDate purchaseDate, int cutoffDay, int paymentDay) {
        var base = purchaseDate.getDayOfMonth() <= cutoffDay ? purchaseDate : purchaseDate.plusMonths(1);
        var anchor = atDay(base, paymentDay);
        if (!anchor.isAfter(purchaseDate)) {
            anchor = atDay(base.plusMonths(1), paymentDay);
        }
        return anchor;
    }

    public static int graceDays(LocalDate purchaseDate, int cutoffDay, int paymentDay) {
        return (int) Math.max(0, ChronoUnit.DAYS.between(purchaseDate, graceEndDate(purchaseDate, cutoffDay, paymentDay)));
    }

    private static LocalDate atDay(LocalDate month, int day) {
        return month.withDayOfMonth(Math.min(day, month.lengthOfMonth()));
    }
}
