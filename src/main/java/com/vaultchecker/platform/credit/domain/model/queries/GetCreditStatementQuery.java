package com.vaultchecker.platform.credit.domain.model.queries;

import java.time.LocalDate;

public record GetCreditStatementQuery(String customerId, LocalDate asOfDate) {
}
