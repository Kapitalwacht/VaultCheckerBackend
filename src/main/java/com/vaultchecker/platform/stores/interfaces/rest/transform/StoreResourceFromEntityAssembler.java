package com.vaultchecker.platform.stores.interfaces.rest.transform;

import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.interfaces.rest.resources.StoreResource;

public class StoreResourceFromEntityAssembler {
    public static StoreResource toResourceFromEntity(Store store) {
        return new StoreResource(store.getId(), store.getStoreId(), store.getRuc(), store.getBusinessName(),
                store.getCategory(), store.getAddress(), store.getPhone(), store.getEmail(),
                store.getDescription(), store.getState());
    }
}
