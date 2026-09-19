package com.vaultchecker.platform.subscriptions.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Plan extends AbstractDomainAggregateRoot<Plan> {

    @Setter
    private Long id;
    @Setter
    private String planId;
    @Setter
    private String name;
    @Setter
    private double priceMonthly;
    @Setter
    private String currency;
    @Setter
    private List<String> features;
    @Setter
    private String stripePaymentLink;
    @Setter
    private boolean highlighted;

    public Plan() {
        this.features = new ArrayList<>();
        this.currency = "PEN";
    }
}
