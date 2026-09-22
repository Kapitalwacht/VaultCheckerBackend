package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers.CreditAccountPersistenceAssembler;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.repositories.CreditAccountPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CreditAccountRepositoryImpl implements CreditAccountRepository {

    private final CreditAccountPersistenceRepository repository;

    public CreditAccountRepositoryImpl(CreditAccountPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CreditAccount> findById(Long id) {
        return repository.findById(id).map(CreditAccountPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<CreditAccount> findAll() {
        return repository.findAll().stream().map(CreditAccountPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<CreditAccount> findAllByStoreId(String storeId) {
        return repository.findAllByStoreId(storeId).stream().map(CreditAccountPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<CreditAccount> findAllByCustomerId(String customerId) {
        return repository.findAllByCustomerId(customerId).stream().map(CreditAccountPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public CreditAccount save(CreditAccount creditAccount) {
        var saved = repository.save(CreditAccountPersistenceAssembler.toPersistenceFromDomain(creditAccount));
        return CreditAccountPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
