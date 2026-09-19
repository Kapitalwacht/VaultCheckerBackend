package com.vaultchecker.platform.subscriptions.application.internal.queryservices;

import com.vaultchecker.platform.subscriptions.application.queryservices.BillingQueryService;
import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Invoice;
import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Plan;
import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Subscription;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers.InvoicePersistenceAssembler;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.InvoicePersistenceRepository;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillingQueryServiceImpl implements BillingQueryService {

    private final PlanPersistenceRepository planPersistenceRepository;
    private final SubscriptionPersistenceRepository subscriptionPersistenceRepository;
    private final InvoicePersistenceRepository invoicePersistenceRepository;

    public BillingQueryServiceImpl(PlanPersistenceRepository planPersistenceRepository,
                                   SubscriptionPersistenceRepository subscriptionPersistenceRepository,
                                   InvoicePersistenceRepository invoicePersistenceRepository) {
        this.planPersistenceRepository = planPersistenceRepository;
        this.subscriptionPersistenceRepository = subscriptionPersistenceRepository;
        this.invoicePersistenceRepository = invoicePersistenceRepository;
    }

    @Override
    public List<Plan> getAllPlans() {
        return planPersistenceRepository.findAll().stream()
                .map(PlanPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Subscription> getSubscriptionByStore(String storeId) {
        return subscriptionPersistenceRepository.findByStoreId(storeId)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Invoice> getInvoicesByStore(String storeId) {
        return invoicePersistenceRepository.findByStoreIdOrderByDateDesc(storeId).stream()
                .map(InvoicePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
