package com.vaultchecker.platform.subscriptions.interfaces.rest.transform;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Plan;
import com.vaultchecker.platform.subscriptions.interfaces.rest.resources.PlanResource;

public class PlanResourceFromEntityAssembler {
    public static PlanResource toResourceFromEntity(Plan plan) {
        return new PlanResource(plan.getId(), plan.getPlanId(), plan.getName(), plan.getPriceMonthly(),
                plan.getCurrency(), plan.getFeatures(), plan.getStripePaymentLink(), plan.isHighlighted());
    }
}
