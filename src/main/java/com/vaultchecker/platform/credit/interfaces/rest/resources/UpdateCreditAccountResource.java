package com.vaultchecker.platform.credit.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resource received to update a credit account.
 */
public record UpdateCreditAccountResource(
        @Size(max = 60) String creditId,
        @Size(max = 60) String storeId,
        @Size(max = 60) String customerId,
        BigDecimal balance,
        BigDecimal creditLimit,
        LocalDate dueDate,
        @Size(max = 20) String state
) {
}
