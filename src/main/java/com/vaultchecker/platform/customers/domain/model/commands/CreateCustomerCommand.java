package com.vaultchecker.platform.customers.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to register a new customer (US-09).
 */
public record CreateCustomerCommand(String customerId, String storeId, String firstName, String lastName,
                                    String dni, String phone, String address, BigDecimal creditLimit) {
}
