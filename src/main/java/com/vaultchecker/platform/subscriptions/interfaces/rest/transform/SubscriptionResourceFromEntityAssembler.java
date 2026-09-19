package com.vaultchecker.platform.subscriptions.interfaces.rest.transform;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Subscription;
import com.vaultchecker.platform.subscriptions.interfaces.rest.resources.SubscriptionResource;

public class SubscriptionResourceFromEntityAssembler {
    public static SubscriptionResource toResourceFromEntity(Subscription subscription) {
        return new SubscriptionResource(subscription.getId(), subscription.getSubscriptionId(),
                subscription.getStoreId(), subscription.getPlanId(), subscription.getStatus(),
                subscription.getCurrentPeriodEnd(), subscription.getStripeCustomerId());
    }
}
