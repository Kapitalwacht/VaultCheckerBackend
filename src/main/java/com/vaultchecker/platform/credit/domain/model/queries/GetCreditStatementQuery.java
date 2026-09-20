package com.vaultchecker.platform.credit.domain.model.queries;

import java.time.LocalDate;

/**
 * Query to build a customer's cutoff-date statement (US-21).
 *
 * @param customerId the customer whose statement is requested
 * @param asOfDate   the reference date used to compute moratory interest; {@code null} means today
 */
public record GetCreditStatementQuery(String customerId, LocalDate asOfDate) {
}
