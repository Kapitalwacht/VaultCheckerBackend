package com.vaultchecker.platform.credit.application.queryservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllPurchasesQuery;

import java.util.List;

public interface PurchaseQueryService {
    List<Purchase> handle(GetAllPurchasesQuery query);
}
