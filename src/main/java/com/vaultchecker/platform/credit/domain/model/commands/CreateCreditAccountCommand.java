package com.vaultchecker.platform.credit.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to open a credit account for a customer.
 */
public record CreateCreditAccountCommand(String creditId, String storeId, String customerId,
                                         BigDecimal balance, BigDecimal creditLimit, LocalDate dueDate) {
}
