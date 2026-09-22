package com.vaultchecker.platform.stores.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.domain.repositories.StoreRepository;
import com.vaultchecker.platform.stores.infrastructure.persistence.jpa.assemblers.StorePersistenceAssembler;
import com.vaultchecker.platform.stores.infrastructure.persistence.jpa.repositories.StorePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StoreRepositoryImpl implements StoreRepository {

    private final StorePersistenceRepository storePersistenceRepository;

    public StoreRepositoryImpl(StorePersistenceRepository storePersistenceRepository) {
        this.storePersistenceRepository = storePersistenceRepository;
    }

    @Override
    public Optional<Store> findById(Long id) {
        return storePersistenceRepository.findById(id).map(StorePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Store> findAll() {
        return storePersistenceRepository.findAll().stream().map(StorePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Store save(Store store) {
        var saved = storePersistenceRepository.save(StorePersistenceAssembler.toPersistenceFromDomain(store));
        return StorePersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsByRuc(String ruc) {
        return storePersistenceRepository.existsByRuc(ruc);
    }

    @Override
    public boolean existsByStoreId(String storeId) {
        return storePersistenceRepository.existsByStoreId(storeId);
    }
}
