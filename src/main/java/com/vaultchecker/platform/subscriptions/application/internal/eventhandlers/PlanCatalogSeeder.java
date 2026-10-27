package com.vaultchecker.platform.subscriptions.application.internal.eventhandlers;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Plan;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanCatalogSeeder {

    private final PlanPersistenceRepository planPersistenceRepository;

    public PlanCatalogSeeder(PlanPersistenceRepository planPersistenceRepository) {
        this.planPersistenceRepository = planPersistenceRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedDefaultPlans() {
        defaultPlans().stream()
                .filter(plan -> !planPersistenceRepository.existsByPlanId(plan.getPlanId()))
                .map(PlanPersistenceAssembler::toPersistenceFromDomain)
                .forEach(planPersistenceRepository::save);
    }

    private static List<Plan> defaultPlans() {
        return List.of(
                plan("barrio", "Barrio", 0, false, List.of(
                        "billing.features.store1",
                        "billing.features.customers50",
                        "billing.features.installments")),
                plan("negocio", "Negocio", 29.9, true, List.of(
                        "billing.features.stores5",
                        "billing.features.customersUnlimited",
                        "billing.features.installments",
                        "billing.features.cutoffReport",
                        "billing.features.fullAudit")),
                plan("cadena", "Cadena", 59.9, false, List.of(
                        "billing.features.storesUnlimited",
                        "billing.features.customersUnlimited",
                        "billing.features.rolesPermissions",
                        "billing.features.prioritySupport",
                        "billing.features.exportReports")));
    }

    private static Plan plan(String planId, String name, double priceMonthly, boolean highlighted, List<String> features) {
        var plan = new Plan();
        plan.setPlanId(planId);
        plan.setName(name);
        plan.setPriceMonthly(priceMonthly);
        plan.setCurrency("PEN");
        plan.setHighlighted(highlighted);
        plan.setFeatures(features);
        plan.setStripePaymentLink("");
        return plan;
    }
}
