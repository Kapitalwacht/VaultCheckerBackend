package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities.CreditAccountPersistenceEntity;

/**
 * Static assembler between credit account domain and persistence representations.
 */
public final class CreditAccountPersistenceAssembler {

    private CreditAccountPersistenceAssembler() {
    }

    public static CreditAccount toDomainFromPersistence(CreditAccountPersistenceEntity entity) {
        if (entity == null) return null;
        var account = new CreditAccount();
        account.setId(entity.getId());
        account.setCreditId(entity.getCreditId());
        account.setStoreId(entity.getStoreId());
        account.setCustomerId(entity.getCustomerId());
        account.setBalance(entity.getBalance());
        account.setCreditLimit(entity.getCreditLimit());
        account.setDueDate(entity.getDueDate());
        account.setState(entity.getState());
        return account;
    }

    public static CreditAccountPersistenceEntity toPersistenceFromDomain(CreditAccount account) {
        if (account == null) return null;
        var entity = new CreditAccountPersistenceEntity();
        if (account.getId() != null) {
            entity.setId(account.getId());
        }
        entity.setCreditId(account.getCreditId());
        entity.setStoreId(account.getStoreId());
        entity.setCustomerId(account.getCustomerId());
        entity.setBalance(account.getBalance());
        entity.setCreditLimit(account.getCreditLimit());
        entity.setDueDate(account.getDueDate());
        entity.setState(account.getState());
        return entity;
    }
}
