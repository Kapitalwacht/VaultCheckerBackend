package com.vaultchecker.platform.stores.application.queryservices;

import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.domain.model.queries.GetAllStoresQuery;
import com.vaultchecker.platform.stores.domain.model.queries.GetStoreByIdQuery;

import java.util.List;
import java.util.Optional;

public interface StoreQueryService {
    List<Store> handle(GetAllStoresQuery query);

    Optional<Store> handle(GetStoreByIdQuery query);
}
