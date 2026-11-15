package com.vaultchecker.platform.subscriptions.application.internal.eventhandlers;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Plan;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.InvoicePersistenceRepository;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlanCatalogSeeder {

    /** Plan ids used before the catalog switched to English ids, mapped to their current id. */
    private static final Map<String, String> LEGACY_PLAN_IDS = Map.of(
            "barrio", Plan.DEFAULT_PLAN_ID,
            "negocio", "business",
            "cadena", "chain");

    private final PlanPersistenceRepository planPersistenceRepository;
    private final SubscriptionPersistenceRepository subscriptionPersistenceRepository;
    private final InvoicePersistenceRepository invoicePersistenceRepository;
    private final String businessPaymentLink;
    private final String chainPaymentLink;

    public PlanCatalogSeeder(PlanPersistenceRepository planPersistenceRepository,
                             SubscriptionPersistenceRepository subscriptionPersistenceRepository,
                             InvoicePersistenceRepository invoicePersistenceRepository,
                             @Value("${stripe.payment-link.business:}") String businessPaymentLink,
                             @Value("${stripe.payment-link.chain:}") String chainPaymentLink) {
        this.planPersistenceRepository = planPersistenceRepository;
        this.subscriptionPersistenceRepository = subscriptionPersistenceRepository;
        this.invoicePersistenceRepository = invoicePersistenceRepository;
        this.businessPaymentLink = businessPaymentLink;
        this.chainPaymentLink = chainPaymentLink;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedDefaultPlans() {
        migrateLegacyPlanIds();
        defaultPlans().forEach(plan -> {
            var seeded = PlanPersistenceAssembler.toPersistenceFromDomain(plan);
            planPersistenceRepository.findByPlanId(plan.getPlanId())
                    .ifPresentOrElse(existing -> {
                        existing.setName(seeded.getName());
                        existing.setPriceMonthly(seeded.getPriceMonthly());
                        existing.setFeatures(seeded.getFeatures());
                        existing.setHighlighted(seeded.isHighlighted());
                        existing.setStripePaymentLink(seeded.getStripePaymentLink());
                        planPersistenceRepository.save(existing);
                    }, () -> planPersistenceRepository.save(seeded));
        });
    }

    private void migrateLegacyPlanIds() {
        LEGACY_PLAN_IDS.forEach((legacyId, planId) -> {
            planPersistenceRepository.findByPlanId(legacyId).ifPresent(legacy -> {
                if (planPersistenceRepository.existsByPlanId(planId)) {
                    planPersistenceRepository.delete(legacy);
                } else {
                    legacy.setPlanId(planId);
                    planPersistenceRepository.save(legacy);
                }
            });
            subscriptionPersistenceRepository.renamePlanId(legacyId, planId);
            invoicePersistenceRepository.renamePlanId(legacyId, planId);
        });
    }

    private List<Plan> defaultPlans() {
        return List.of(
                plan(Plan.DEFAULT_PLAN_ID, "Corner", 0, false, "", List.of(
                        "billing.features.store1",
                        "billing.features.customers50",
                        "billing.features.installments")),
                plan("business", "Business", 30, true, businessPaymentLink, List.of(
                        "billing.features.stores5",
                        "billing.features.customersUnlimited",
                        "billing.features.installments",
                        "billing.features.cutoffReport",
                        "billing.features.fullAudit")),
                plan("chain", "Chain", 75, false, chainPaymentLink, List.of(
                        "billing.features.storesUnlimited",
                        "billing.features.customersUnlimited",
                        "billing.features.rolesPermissions",
                        "billing.features.prioritySupport",
                        "billing.features.exportReports")));
    }

    private static Plan plan(String planId, String name, double priceMonthly, boolean highlighted,
                             String stripePaymentLink, List<String> features) {
        var plan = new Plan();
        plan.setPlanId(planId);
        plan.setName(name);
        plan.setPriceMonthly(priceMonthly);
        plan.setCurrency("PEN");
        plan.setHighlighted(highlighted);
        plan.setFeatures(features);
        plan.setStripePaymentLink(stripePaymentLink == null ? "" : stripePaymentLink);
        return plan;
    }
}
