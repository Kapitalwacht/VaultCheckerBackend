package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditTerms;
import com.vaultchecker.platform.credit.interfaces.rest.resources.PurchaseResource;

public class PurchaseResourceFromEntityAssembler {
    public static PurchaseResource toResourceFromEntity(Purchase purchase) {
        return new PurchaseResource(purchase.getId(), purchase.getPurchaseId(), purchase.getStoreId(),
                purchase.getCustomerId(), purchase.getProductId(), purchase.getDescription(), purchase.getQuantity(),
                purchase.getAmount(), purchase.getPurchaseDate(), purchase.getMonths(), purchase.getState(),
                toResourceFromTerms(purchase.getTerms()));
    }

    private static PurchaseResource.CreditTermsResource toResourceFromTerms(CreditTerms terms) {
        if (terms == null) return null;
        return new PurchaseResource.CreditTermsResource(terms.rateType(), terms.rateValue(), terms.ratePeriodDays(),
                terms.rateCapitalizationDays(), terms.moratoriumRateType(), terms.moratoriumRateValue(),
                terms.cutoffDay(), terms.paymentDay());
    }
}
