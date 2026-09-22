package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditStatement;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreditStatementResource;

public class CreditStatementResourceFromEntityAssembler {
    public static CreditStatementResource toResourceFromEntity(CreditStatement statement) {
        var lines = statement.lines().stream()
                .map(line -> new CreditStatementResource.LineResource(
                        line.purchaseId(), line.description(), line.purchaseDate(),
                        line.amount(), line.compensatoryDays(), line.compensatoryInterest()))
                .toList();
        return new CreditStatementResource(
                statement.customerId(),
                statement.storeId(),
                statement.dueDate(),
                statement.state(),
                lines,
                statement.totalPrincipal(),
                statement.totalCompensatoryInterest(),
                statement.moratoryInterest(),
                statement.totalToPay());
    }
}
