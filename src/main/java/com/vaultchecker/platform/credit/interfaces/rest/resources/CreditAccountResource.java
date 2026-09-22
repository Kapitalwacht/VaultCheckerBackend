package com.vaultchecker.platform.credit.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditAccountResource(Long id, String creditId, String storeId, String customerId,
                                    BigDecimal balance, BigDecimal creditLimit, LocalDate dueDate, String state) {
}
