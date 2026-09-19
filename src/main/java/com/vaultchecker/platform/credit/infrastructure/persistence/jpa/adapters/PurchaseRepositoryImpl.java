package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers.PurchasePersistenceAssembler;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.repositories.PurchasePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PurchaseRepositoryImpl implements PurchaseRepository {

    private final PurchasePersistenceRepository repository;

    public PurchaseRepositoryImpl(PurchasePersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Purchase save(Purchase purchase) {
        var saved = repository.save(PurchasePersistenceAssembler.toPersistenceFromDomain(purchase));
        return PurchasePersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public List<Purchase> findAll() {
        return repository.findAll().stream().map(PurchasePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Purchase> findAllByStoreId(String storeId) {
        return repository.findAllByStoreId(storeId).stream()
                .map(PurchasePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Purchase> findAllByCustomerId(String customerId) {
        return repository.findAllByCustomerId(customerId).stream()
                .map(PurchasePersistenceAssembler::toDomainFromPersistence).toList();
    }
}
