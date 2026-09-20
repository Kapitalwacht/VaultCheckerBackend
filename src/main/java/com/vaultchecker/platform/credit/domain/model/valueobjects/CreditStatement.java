package com.vaultchecker.platform.credit.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * A customer's cutoff-date statement (listado de pago): the fin-de-mes purchases ordered by date with
 * their accumulated compensatory interest, plus any moratory interest, and the total to pay (US-21).
 *
 * @param customerId                 the customer the statement belongs to
 * @param storeId                    the store that granted the credit
 * @param dueDate                    the pactada payment date
 * @param state                      the credit account state (current / overdue)
 * @param lines                      one line per fin-de-mes purchase
 * @param totalPrincipal             outstanding principal (credit account balance)
 * @param totalCompensatoryInterest  sum of the compensatory interest across the lines
 * @param moratoryInterest           moratory interest accrued when the account is overdue
 * @param totalToPay                 principal + compensatory + moratory interest
 */
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

    /**
     * One statement line for a fin-de-mes purchase.
     *
     * @param purchaseId            the purchase identifier
     * @param description           the purchase description
     * @param purchaseDate          the date the purchase was made
     * @param amount                the purchase amount
     * @param compensatoryDays      days between the purchase and the payment date
     * @param compensatoryInterest  compensatory interest accrued over those days
     */
    public record Line(
            String purchaseId,
            String description,
            LocalDate purchaseDate,
            BigDecimal amount,
            int compensatoryDays,
            BigDecimal compensatoryInterest) {
    }
}
