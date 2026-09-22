package com.vaultchecker.platform.credit.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreditStatementResource(
        String customerId,
        String storeId,
        LocalDate dueDate,
        String state,
        List<LineResource> lines,
        BigDecimal totalPrincipal,
        BigDecimal totalCompensatoryInterest,
        BigDecimal moratoryInterest,
        BigDecimal totalToPay) {

    public record LineResource(
            String purchaseId,
            String description,
            LocalDate purchaseDate,
            BigDecimal amount,
            Integer compensatoryDays,
            BigDecimal compensatoryInterest) {
    }
}
