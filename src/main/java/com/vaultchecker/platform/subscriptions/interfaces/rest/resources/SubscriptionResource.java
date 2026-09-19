package com.vaultchecker.platform.subscriptions.interfaces.rest.resources;

public record SubscriptionResource(Long id, String subscriptionId, String storeId, String planId,
                                   String status, String currentPeriodEnd, String stripeCustomerId) {
}
