package com.vaultchecker.platform.credit.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCreditAccountCommand(Long id, String creditId, String storeId, String customerId,
                                         BigDecimal balance, BigDecimal creditLimit, LocalDate dueDate, String state) {
}
