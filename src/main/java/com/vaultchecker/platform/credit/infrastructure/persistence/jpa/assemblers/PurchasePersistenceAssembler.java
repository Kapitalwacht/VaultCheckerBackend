package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities.PurchasePersistenceEntity;

public final class PurchasePersistenceAssembler {

    private PurchasePersistenceAssembler() {
    }

    public static Purchase toDomainFromPersistence(PurchasePersistenceEntity entity) {
        if (entity == null) return null;
        var purchase = new Purchase();
        purchase.setId(entity.getId());
        purchase.setPurchaseId(entity.getPurchaseId());
        purchase.setStoreId(entity.getStoreId());
        purchase.setCustomerId(entity.getCustomerId());
        purchase.setProductId(entity.getProductId());
        purchase.setDescription(entity.getDescription());
        purchase.setQuantity(entity.getQuantity());
        purchase.setAmount(entity.getAmount());
        purchase.setPurchaseDate(entity.getPurchaseDate());
        purchase.setMonths(entity.getMonths());
        purchase.setState(entity.getState());
        return purchase;
    }

    public static PurchasePersistenceEntity toPersistenceFromDomain(Purchase purchase) {
        if (purchase == null) return null;
        var entity = new PurchasePersistenceEntity();
        if (purchase.getId() != null) {
            entity.setId(purchase.getId());
        }
        entity.setPurchaseId(purchase.getPurchaseId());
        entity.setStoreId(purchase.getStoreId());
        entity.setCustomerId(purchase.getCustomerId());
        entity.setProductId(purchase.getProductId());
        entity.setDescription(purchase.getDescription());
        entity.setQuantity(purchase.getQuantity());
        entity.setAmount(purchase.getAmount());
        entity.setPurchaseDate(purchase.getPurchaseDate());
        entity.setMonths(purchase.getMonths());
        entity.setState(purchase.getState());
        return entity;
    }
}
