package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditStatement;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreditStatementResource;

public class CreditStatementResourceFromEntityAssembler {
    public static CreditStatementResource toResourceFromEntity(CreditStatement statement) {
        var lines = statement.lines().stream()
                .map(line -> new CreditStatementResource.LineResource(
                        line.purchaseId(), line.description(), line.purchaseDate(), line.months(), line.period(),
                        line.dueDate(), line.interest(), line.principal(), line.total()))
                .toList();
        var lateFees = statement.lateFees().stream()
                .map(fee -> new CreditStatementResource.LateFeeResource(
                        fee.purchaseId(), fee.description(), fee.period(), fee.dueDate(), fee.daysLate(),
                        fee.overdueAmount(), fee.rate(), fee.amount()))
                .toList();
        return new CreditStatementResource(
                statement.customerId(),
                statement.storeId(),
                statement.cutoffDate(),
                statement.paymentDate(),
                lines,
                lateFees,
                statement.totalInterest(),
                statement.totalPrincipal(),
                statement.totalLateFee(),
                statement.totalToPay());
    }
}
