package com.vaultchecker.platform.credit.domain.model.valueobjects;

import com.vaultchecker.platform.credit.domain.services.CreditCycle;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditTerms(
        String rateType,
        BigDecimal rateValue,
        Integer ratePeriodDays,
        Integer rateCapitalizationDays,
        String moratoriumRateType,
        BigDecimal moratoriumRateValue,
        Integer cutoffDay,
        Integer paymentDay) {

    public static CreditTerms from(Customer customer) {
        return new CreditTerms(customer.getRateType(), customer.getRateValue(), customer.getRatePeriodDays(),
                customer.getRateCapitalizationDays(), customer.getMoratoriumRateType(),
                customer.getMoratoriumRateValue(), customer.getCutoffDay(), customer.getPaymentDay());
    }

    public BigDecimal compensatoryRate() {
        return CreditCycle.effectiveAnnualRate(rateType, rateValue, ratePeriodDays, rateCapitalizationDays);
    }

    public BigDecimal moratoryRate() {
        return CreditCycle.effectiveAnnualRate(moratoriumRateType, moratoriumRateValue, ratePeriodDays, rateCapitalizationDays);
    }

    public LocalDate firstPaymentDate(LocalDate purchaseDate) {
        return CreditCycle.graceEndDate(purchaseDate, CreditCycle.cutoffDayOf(cutoffDay), CreditCycle.paymentDayOf(paymentDay));
    }
}
