package com.vaultchecker.platform.subscriptions.application.internal.eventhandlers;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Plan;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanCatalogSeeder {

    private final PlanPersistenceRepository planPersistenceRepository;
    private final String negocioPaymentLink;
    private final String cadenaPaymentLink;

    public PlanCatalogSeeder(PlanPersistenceRepository planPersistenceRepository,
                             @Value("${stripe.payment-link.negocio:}") String negocioPaymentLink,
                             @Value("${stripe.payment-link.cadena:}") String cadenaPaymentLink) {
        this.planPersistenceRepository = planPersistenceRepository;
        this.negocioPaymentLink = negocioPaymentLink;
        this.cadenaPaymentLink = cadenaPaymentLink;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedDefaultPlans() {
        defaultPlans().forEach(plan -> planPersistenceRepository.findByPlanId(plan.getPlanId())
                .ifPresentOrElse(existing -> {
                    existing.setPriceMonthly(plan.getPriceMonthly());
                    existing.setStripePaymentLink(plan.getStripePaymentLink());
                    planPersistenceRepository.save(existing);
                }, () -> planPersistenceRepository.save(PlanPersistenceAssembler.toPersistenceFromDomain(plan))));
    }

    private List<Plan> defaultPlans() {
        return List.of(
                plan("barrio", "Barrio", 0, false, "", List.of(
                        "billing.features.store1",
                        "billing.features.customers50",
                        "billing.features.installments")),
                plan("negocio", "Negocio", 30, true, negocioPaymentLink, List.of(
                        "billing.features.stores5",
                        "billing.features.customersUnlimited",
                        "billing.features.installments",
                        "billing.features.cutoffReport",
                        "billing.features.fullAudit")),
                plan("cadena", "Cadena", 75, false, cadenaPaymentLink, List.of(
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
