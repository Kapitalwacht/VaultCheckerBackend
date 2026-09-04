package com.vaultchecker.platform.credit.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resource representing a credit account returned by the REST API. Field names match the frontend.
 */
public record CreditAccountResource(Long id, String creditId, String storeId, String customerId,
                                    BigDecimal balance, BigDecimal creditLimit, LocalDate dueDate, String state) {
}
