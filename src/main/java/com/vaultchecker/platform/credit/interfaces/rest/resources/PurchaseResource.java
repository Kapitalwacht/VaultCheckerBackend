package com.vaultchecker.platform.credit.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseResource(Long id, String purchaseId, String storeId, String customerId, String productId,
                               String description, Integer quantity, BigDecimal amount, LocalDate purchaseDate,
                               Integer months, String state, CreditTermsResource terms) {

    public record CreditTermsResource(String rateType, BigDecimal rateValue, Integer ratePeriodDays,
                                      Integer rateCapitalizationDays, String moratoriumRateType,
                                      BigDecimal moratoriumRateValue, Integer cutoffDay, Integer paymentDay) {
    }
}
