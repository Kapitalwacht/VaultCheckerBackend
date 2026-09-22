package com.vaultchecker.platform.credit.application.internal.queryservices;

import com.vaultchecker.platform.credit.application.queryservices.CreditAccountQueryService;
import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllCreditAccountsQuery;
import com.vaultchecker.platform.credit.domain.model.queries.GetCreditAccountByIdQuery;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditAccountQueryServiceImpl implements CreditAccountQueryService {

    private final CreditAccountRepository creditAccountRepository;

    public CreditAccountQueryServiceImpl(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    @Override
    public List<CreditAccount> handle(GetAllCreditAccountsQuery query) {
        if (query.customerId() != null && !query.customerId().isBlank()) {
            return creditAccountRepository.findAllByCustomerId(query.customerId());
        }
        if (query.storeId() != null && !query.storeId().isBlank()) {
            return creditAccountRepository.findAllByStoreId(query.storeId());
        }
        return creditAccountRepository.findAll();
    }

    @Override
    public Optional<CreditAccount> handle(GetCreditAccountByIdQuery query) {
        return creditAccountRepository.findById(query.id());
    }
}
