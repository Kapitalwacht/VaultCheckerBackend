package com.vaultchecker.platform.credit.domain.repositories;

import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;

import java.util.List;
import java.util.Optional;

/**
 * Credit account repository port.
 */
public interface CreditAccountRepository {
    Optional<CreditAccount> findById(Long id);

    List<CreditAccount> findAll();

    List<CreditAccount> findAllByStoreId(String storeId);

    List<CreditAccount> findAllByCustomerId(String customerId);

    CreditAccount save(CreditAccount creditAccount);

    void deleteById(Long id);

    boolean existsById(Long id);
}
