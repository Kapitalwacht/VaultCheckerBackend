package com.vaultchecker.platform.credit.application.queryservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllCreditAccountsQuery;
import com.vaultchecker.platform.credit.domain.model.queries.GetCreditAccountByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for credit account read queries.
 */
public interface CreditAccountQueryService {
    List<CreditAccount> handle(GetAllCreditAccountsQuery query);

    Optional<CreditAccount> handle(GetCreditAccountByIdQuery query);
}
