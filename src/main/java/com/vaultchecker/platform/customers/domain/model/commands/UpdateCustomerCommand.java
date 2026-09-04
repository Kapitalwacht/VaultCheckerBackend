package com.vaultchecker.platform.customers.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to update an existing customer.
 */
public record UpdateCustomerCommand(Long id, String customerId, String storeId, String firstName, String lastName,
                                    String dni, String phone, String address, BigDecimal creditLimit, String state) {
}
