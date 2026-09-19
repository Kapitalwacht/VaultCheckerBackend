package com.vaultchecker.platform.credit.domain.repositories;

import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;

import java.util.List;

public interface PurchaseRepository {
    Purchase save(Purchase purchase);

    List<Purchase> findAll();

    List<Purchase> findAllByStoreId(String storeId);

    List<Purchase> findAllByCustomerId(String customerId);
}
