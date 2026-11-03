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

    public static int cutoffDayOf(Integer cutoffDay) {
        return cutoffDay != null ? cutoffDay : FinanceConstants.DEFAULT_CUTOFF_DAY;
    }

    public static int paymentDayOf(Integer paymentDay) {
        return paymentDay != null ? paymentDay : FinanceConstants.DEFAULT_PAYMENT_DAY;
    }

    public static LocalDate graceEndDate(LocalDate purchaseDate, int cutoffDay, int paymentDay) {
        var cutoff = atDay(purchaseDate, cutoffDay);
        if (purchaseDate.isAfter(cutoff)) {
            cutoff = atDay(purchaseDate.plusMonths(1), cutoffDay);
        }
        var payment = atDay(cutoff, paymentDay);
        return payment.isAfter(cutoff) ? payment : atDay(cutoff.plusMonths(1), paymentDay);
    }

    public static LocalDate installmentDate(LocalDate firstPaymentDate, int period, int paymentDay) {
        return atDay(firstPaymentDate.plusMonths(period), paymentDay);
    }

    public static int graceDays(LocalDate purchaseDate, int cutoffDay, int paymentDay) {
        return (int) Math.max(0, ChronoUnit.DAYS.between(purchaseDate, graceEndDate(purchaseDate, cutoffDay, paymentDay)));
    }

    private static LocalDate atDay(LocalDate month, int day) {
        return month.withDayOfMonth(Math.min(day, month.lengthOfMonth()));
    }
}
