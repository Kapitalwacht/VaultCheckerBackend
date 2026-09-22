package com.vaultchecker.platform.stores.domain.repositories;

import com.vaultchecker.platform.stores.domain.model.aggregates.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository {
    Optional<Store> findById(Long id);

    List<Store> findAll();

    Store save(Store store);

    boolean existsByRuc(String ruc);

    boolean existsByStoreId(String storeId);
}
