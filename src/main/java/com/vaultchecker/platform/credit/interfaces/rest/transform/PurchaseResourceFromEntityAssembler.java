package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.interfaces.rest.resources.PurchaseResource;

public class PurchaseResourceFromEntityAssembler {
    public static PurchaseResource toResourceFromEntity(Purchase purchase) {
        return new PurchaseResource(purchase.getId(), purchase.getPurchaseId(), purchase.getStoreId(),
                purchase.getCustomerId(), purchase.getProductId(), purchase.getDescription(), purchase.getQuantity(),
                purchase.getAmount(), purchase.getPurchaseDate(), purchase.getMonths(), purchase.getState());
    }
}
