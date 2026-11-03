package com.vaultchecker.platform.credit.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreditStatementResource(
        String customerId,
        String storeId,
        LocalDate cutoffDate,
        LocalDate paymentDate,
        List<LineResource> lines,
        List<LateFeeResource> lateFees,
        BigDecimal totalInterest,
        BigDecimal totalPrincipal,
        BigDecimal totalLateFee,
        BigDecimal totalToPay) {

    public record LineResource(
            String purchaseId,
            String description,
            LocalDate purchaseDate,
            Integer months,
            Integer period,
            LocalDate dueDate,
            BigDecimal interest,
            BigDecimal principal,
            BigDecimal total) {
    }

    public record LateFeeResource(
            String purchaseId,
            String description,
            Integer period,
            LocalDate dueDate,
            Integer daysLate,
            BigDecimal overdueAmount,
            BigDecimal rate,
            BigDecimal amount) {
    }
}
