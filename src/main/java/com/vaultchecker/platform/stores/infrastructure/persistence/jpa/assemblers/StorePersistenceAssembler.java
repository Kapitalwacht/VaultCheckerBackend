package com.vaultchecker.platform.stores.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.infrastructure.persistence.jpa.entities.StorePersistenceEntity;

public final class StorePersistenceAssembler {

    private StorePersistenceAssembler() {
    }

    public static Store toDomainFromPersistence(StorePersistenceEntity entity) {
        if (entity == null) return null;
        var store = new Store();
        store.setId(entity.getId());
        store.setStoreId(entity.getStoreId());
        store.setRuc(entity.getRuc());
        store.setBusinessName(entity.getBusinessName());
        store.setCategory(entity.getCategory());
        store.setAddress(entity.getAddress());
        store.setPhone(entity.getPhone());
        store.setEmail(entity.getEmail());
        store.setDescription(entity.getDescription());
        store.setState(entity.getState());
        return store;
    }

    public static StorePersistenceEntity toPersistenceFromDomain(Store store) {
        if (store == null) return null;
        var entity = new StorePersistenceEntity();
        if (store.getId() != null) {
            entity.setId(store.getId());
        }
        entity.setStoreId(store.getStoreId());
        entity.setRuc(store.getRuc());
        entity.setBusinessName(store.getBusinessName());
        entity.setCategory(store.getCategory());
        entity.setAddress(store.getAddress());
        entity.setPhone(store.getPhone());
        entity.setEmail(store.getEmail());
        entity.setDescription(store.getDescription());
        entity.setState(store.getState());
        return entity;
    }
}
