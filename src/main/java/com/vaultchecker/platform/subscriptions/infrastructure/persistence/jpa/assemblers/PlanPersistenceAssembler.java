package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Plan;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PlanPersistenceAssembler {

    private static final String SEPARATOR = "|";

    private PlanPersistenceAssembler() {
    }

    public static Plan toDomainFromPersistence(PlanPersistenceEntity entity) {
        if (entity == null) return null;
        var plan = new Plan();
        plan.setId(entity.getId());
        plan.setPlanId(entity.getPlanId());
        plan.setName(entity.getName());
        plan.setPriceMonthly(entity.getPriceMonthly());
        plan.setCurrency(entity.getCurrency());
        plan.setFeatures(splitFeatures(entity.getFeatures()));
        plan.setStripePaymentLink(entity.getStripePaymentLink());
        plan.setHighlighted(entity.isHighlighted());
        return plan;
    }

    public static PlanPersistenceEntity toPersistenceFromDomain(Plan plan) {
        if (plan == null) return null;
        var entity = new PlanPersistenceEntity();
        if (plan.getId() != null) {
            entity.setId(plan.getId());
        }
        entity.setPlanId(plan.getPlanId());
        entity.setName(plan.getName());
        entity.setPriceMonthly(plan.getPriceMonthly());
        entity.setCurrency(plan.getCurrency());
        entity.setFeatures(joinFeatures(plan.getFeatures()));
        entity.setStripePaymentLink(plan.getStripePaymentLink());
        entity.setHighlighted(plan.isHighlighted());
        return entity;
    }

    private static List<String> splitFeatures(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split("\\" + SEPARATOR)));
    }

    private static String joinFeatures(List<String> features) {
        if (features == null || features.isEmpty()) return "";
        return String.join(SEPARATOR, features);
    }
}
