package com.vaultchecker.platform.iam.infrastructure.stores;

import com.vaultchecker.platform.iam.application.internal.outboundservices.stores.StoreDirectoryService;
import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.domain.repositories.StoreRepository;
import org.springframework.stereotype.Service;

@Service
public class StoreDirectoryServiceImpl implements StoreDirectoryService {

    private final StoreRepository storeRepository;

    public StoreDirectoryServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public boolean isInactive(String storeId) {
        return storeRepository.findAll().stream()
                .anyMatch(store -> store.getStoreId() != null && store.getStoreId().equals(storeId)
                        && Store.INACTIVE.equalsIgnoreCase(store.getState()));
    }

    @Override
    public boolean exists(String storeId) {
        return storeRepository.existsByStoreId(storeId);
    }

    @Override
    public boolean isRucTaken(String ruc) {
        return storeRepository.existsByRuc(ruc);
    }

    @Override
    public void register(String storeId, String ruc, String businessName, String category, String phone, String email) {
        storeRepository.save(new Store(storeId, ruc, businessName, category, null, phone, email, null));
    }
}
