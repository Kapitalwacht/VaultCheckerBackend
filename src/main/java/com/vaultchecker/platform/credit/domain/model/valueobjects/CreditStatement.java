package com.vaultchecker.platform.credit.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreditStatement(
        String customerId,
        String storeId,
        LocalDate dueDate,
        String state,
        List<Line> lines,
        BigDecimal totalPrincipal,
        BigDecimal totalCompensatoryInterest,
        BigDecimal moratoryInterest,
        BigDecimal totalToPay) {

    public record Line(
            String purchaseId,
            String description,
            LocalDate purchaseDate,
            BigDecimal amount,
            int compensatoryDays,
            BigDecimal compensatoryInterest) {
    }
}
