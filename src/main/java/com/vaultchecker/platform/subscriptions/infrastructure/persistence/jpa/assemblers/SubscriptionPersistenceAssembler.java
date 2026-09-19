package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Subscription;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;

public final class SubscriptionPersistenceAssembler {

    private SubscriptionPersistenceAssembler() {
    }

    public static Subscription toDomainFromPersistence(SubscriptionPersistenceEntity entity) {
        if (entity == null) return null;
        var subscription = new Subscription();
        subscription.setId(entity.getId());
        subscription.setSubscriptionId(entity.getSubscriptionId());
        subscription.setStoreId(entity.getStoreId());
        subscription.setPlanId(entity.getPlanId());
        subscription.setStatus(entity.getStatus());
        subscription.setCurrentPeriodEnd(entity.getCurrentPeriodEnd());
        subscription.setStripeCustomerId(entity.getStripeCustomerId());
        return subscription;
    }

    public static SubscriptionPersistenceEntity toPersistenceFromDomain(Subscription subscription) {
        if (subscription == null) return null;
        var entity = new SubscriptionPersistenceEntity();
        if (subscription.getId() != null) {
            entity.setId(subscription.getId());
        }
        entity.setSubscriptionId(subscription.getSubscriptionId());
        entity.setStoreId(subscription.getStoreId());
        entity.setPlanId(subscription.getPlanId());
        entity.setStatus(subscription.getStatus());
        entity.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
        entity.setStripeCustomerId(subscription.getStripeCustomerId());
        return entity;
    }
}
