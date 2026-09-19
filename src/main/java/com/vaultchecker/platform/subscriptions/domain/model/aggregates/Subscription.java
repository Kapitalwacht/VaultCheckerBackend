package com.vaultchecker.platform.subscriptions.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Subscription extends AbstractDomainAggregateRoot<Subscription> {

    @Setter
    private Long id;
    @Setter
    private String subscriptionId;
    @Setter
    private String storeId;
    @Setter
    private String planId;
    @Setter
    private String status;
    @Setter
    private String currentPeriodEnd;
    @Setter
    private String stripeCustomerId;

    public static final String ACTIVE = "active";

    public Subscription() {
        this.status = ACTIVE;
    }

    public Subscription(String subscriptionId, String storeId, String planId, String currentPeriodEnd) {
        this.subscriptionId = subscriptionId;
        this.storeId = storeId;
        this.planId = planId;
        this.currentPeriodEnd = currentPeriodEnd;
        this.status = ACTIVE;
    }
}
