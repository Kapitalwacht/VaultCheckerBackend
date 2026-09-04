package com.vaultchecker.platform.stores.application.internal.queryservices;

import com.vaultchecker.platform.stores.application.queryservices.StoreQueryService;
import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.domain.model.queries.GetAllStoresQuery;
import com.vaultchecker.platform.stores.domain.model.queries.GetStoreByIdQuery;
import com.vaultchecker.platform.stores.domain.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Store query service implementation.
 */
@Service
public class StoreQueryServiceImpl implements StoreQueryService {

    private final StoreRepository storeRepository;

    public StoreQueryServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public List<Store> handle(GetAllStoresQuery query) {
        return storeRepository.findAll();
    }

    @Override
    public Optional<Store> handle(GetStoreByIdQuery query) {
        return storeRepository.findById(query.id());
    }
}
