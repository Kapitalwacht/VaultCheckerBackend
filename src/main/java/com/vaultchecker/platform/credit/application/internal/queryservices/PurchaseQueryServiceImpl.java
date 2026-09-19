package com.vaultchecker.platform.credit.application.internal.queryservices;

import com.vaultchecker.platform.credit.application.queryservices.PurchaseQueryService;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllPurchasesQuery;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseQueryServiceImpl implements PurchaseQueryService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseQueryServiceImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public List<Purchase> handle(GetAllPurchasesQuery query) {
        if (query.customerId() != null && !query.customerId().isBlank()) {
            return purchaseRepository.findAllByCustomerId(query.customerId());
        }
        if (query.storeId() != null && !query.storeId().isBlank()) {
            return purchaseRepository.findAllByStoreId(query.storeId());
        }
        return purchaseRepository.findAll();
    }
}
