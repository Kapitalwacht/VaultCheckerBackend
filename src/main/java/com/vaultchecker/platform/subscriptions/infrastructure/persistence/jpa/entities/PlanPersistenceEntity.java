package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class PlanPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "plan_id", nullable = false, length = 40)
    private String planId;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "price_monthly", nullable = false)
    private double priceMonthly;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "features", length = 500)
    private String features;

    @Column(name = "stripe_payment_link", length = 300)
    private String stripePaymentLink;

    @Column(name = "highlighted", nullable = false)
    private boolean highlighted = false;
}
