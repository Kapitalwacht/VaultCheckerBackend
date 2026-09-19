package com.vaultchecker.platform.subscriptions.application.queryservices;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Invoice;
import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Plan;
import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Subscription;

import java.util.List;
import java.util.Optional;

public interface BillingQueryService {
    List<Plan> getAllPlans();

    Optional<Subscription> getSubscriptionByStore(String storeId);

    List<Invoice> getInvoicesByStore(String storeId);
}
