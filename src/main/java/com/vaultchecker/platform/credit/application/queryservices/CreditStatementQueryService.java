package com.vaultchecker.platform.credit.application.queryservices;

import com.vaultchecker.platform.credit.domain.model.queries.GetCreditStatementQuery;
import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditStatement;

import java.util.Optional;

public interface CreditStatementQueryService {
    Optional<CreditStatement> handle(GetCreditStatementQuery query);
}
