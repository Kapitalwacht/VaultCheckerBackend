package com.vaultchecker.platform.subscriptions.application.internal.commandservices;

import com.vaultchecker.platform.subscriptions.application.commandservices.BillingCommandService;
import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Subscription;
import com.vaultchecker.platform.subscriptions.domain.model.commands.ChangePlanCommand;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.InvoicePersistenceEntity;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.InvoicePersistenceRepository;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BillingCommandServiceImpl implements BillingCommandService {

    private static final int PERIOD_DAYS = 30;

    private final PlanPersistenceRepository planPersistenceRepository;
    private final SubscriptionPersistenceRepository subscriptionPersistenceRepository;
    private final InvoicePersistenceRepository invoicePersistenceRepository;

    public BillingCommandServiceImpl(PlanPersistenceRepository planPersistenceRepository,
                                     SubscriptionPersistenceRepository subscriptionPersistenceRepository,
                                     InvoicePersistenceRepository invoicePersistenceRepository) {
        this.planPersistenceRepository = planPersistenceRepository;
        this.subscriptionPersistenceRepository = subscriptionPersistenceRepository;
        this.invoicePersistenceRepository = invoicePersistenceRepository;
    }

    @Override
    public Optional<Subscription> handle(ChangePlanCommand command) {
        var plan = planPersistenceRepository.findAll().stream()
                .filter(candidate -> candidate.getPlanId().equalsIgnoreCase(command.planId()))
                .findFirst();
        if (plan.isEmpty()) {
            return Optional.empty();
        }

        var now = LocalDate.now();
        var periodEnd = now.plusDays(PERIOD_DAYS).toString();

        var entity = subscriptionPersistenceRepository.findByStoreId(command.storeId())
                .orElseGet(SubscriptionPersistenceEntity::new);
        if (entity.getSubscriptionId() == null) {
            entity.setSubscriptionId("SU-" + System.currentTimeMillis());
        }
        entity.setStoreId(command.storeId());
        entity.setPlanId(command.planId());
        entity.setStatus(Subscription.ACTIVE);
        entity.setCurrentPeriodEnd(periodEnd);
        var saved = subscriptionPersistenceRepository.save(entity);

        if (plan.get().getPriceMonthly() > 0) {
            recordInvoice(command.storeId(), plan.get(), now.toString());
        }

        return Optional.of(SubscriptionPersistenceAssembler.toDomainFromPersistence(saved));
    }

    private void recordInvoice(String storeId, PlanPersistenceEntity plan, String date) {
        var invoice = new InvoicePersistenceEntity();
        invoice.setInvoiceId("IN-" + System.currentTimeMillis());
        invoice.setStoreId(storeId);
        invoice.setPlanId(plan.getPlanId());
        invoice.setAmount(plan.getPriceMonthly());
        invoice.setCurrency(plan.getCurrency());
        invoice.setStatus("paid");
        invoice.setDate(date);
        invoicePersistenceRepository.save(invoice);
    }
}
