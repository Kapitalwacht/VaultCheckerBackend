package com.vaultchecker.platform.credit.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCreditAccountResource(
        @Size(max = 60) String creditId,
        @Size(max = 60) String storeId,
        @Size(max = 60) String customerId,
        BigDecimal balance,
        BigDecimal creditLimit,
        LocalDate dueDate
) {
}
