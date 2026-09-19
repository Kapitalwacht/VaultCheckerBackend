package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "subscription_id", nullable = false, length = 40)
    private String subscriptionId;

    @Column(name = "store_id", nullable = false, length = 20)
    private String storeId;

    @Column(name = "plan_id", nullable = false, length = 40)
    private String planId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "current_period_end", length = 20)
    private String currentPeriodEnd;

    @Column(name = "stripe_customer_id", length = 60)
    private String stripeCustomerId;
}
