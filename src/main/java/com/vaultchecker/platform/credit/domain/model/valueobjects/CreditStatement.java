package com.vaultchecker.platform.credit.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreditStatement(
        String customerId,
        String storeId,
        LocalDate cutoffDate,
        LocalDate paymentDate,
        List<Line> lines,
        List<LateFee> lateFees,
        BigDecimal totalInterest,
        BigDecimal totalPrincipal,
        BigDecimal totalLateFee,
        BigDecimal totalToPay) {

    public record Line(
            String purchaseId,
            String description,
            LocalDate purchaseDate,
            int months,
            int period,
            LocalDate dueDate,
            BigDecimal interest,
            BigDecimal principal,
            BigDecimal total) {
    }

    public record LateFee(
            String purchaseId,
            String description,
            int period,
            LocalDate dueDate,
            int daysLate,
            BigDecimal overdueAmount,
            BigDecimal rate,
            BigDecimal amount) {
    }
}
