package com.vaultchecker.platform.customers.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to register a new customer with its agreed credit terms.
 */
public record CreateCustomerCommand(String customerId, String storeId, String firstName, String lastName,
                                    String dni, String phone, String address, BigDecimal creditLimit,
                                    String currency, String rateType, BigDecimal rateValue,
                                    Integer rateCapitalizationDays, Integer ratePeriodDays,
                                    String moratoriumRateType, BigDecimal moratoriumRateValue,
                                    Integer maxMonths, Integer cutoffDay, Integer paymentDay) {
}
