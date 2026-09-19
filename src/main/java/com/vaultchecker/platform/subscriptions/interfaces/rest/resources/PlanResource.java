package com.vaultchecker.platform.subscriptions.interfaces.rest.resources;

import java.util.List;

public record PlanResource(Long id, String planId, String name, double priceMonthly, String currency,
                           List<String> features, String stripePaymentLink, boolean highlighted) {
}
