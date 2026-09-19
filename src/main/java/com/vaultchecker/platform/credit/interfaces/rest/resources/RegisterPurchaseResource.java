package com.vaultchecker.platform.credit.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterPurchaseResource(
        @Size(max = 60) String purchaseId,
        @Size(max = 60) String storeId,
        @Size(max = 60) String customerId,
        @Size(max = 60) String productId,
        @Size(max = 200) String description,
        Integer quantity,
        @NotNull BigDecimal amount,
        LocalDate purchaseDate,
        Integer months,
        @Size(max = 20) String state
) {
}
