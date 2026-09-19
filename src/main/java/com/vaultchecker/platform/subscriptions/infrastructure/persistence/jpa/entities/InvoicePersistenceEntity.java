package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class InvoicePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "invoice_id", nullable = false, length = 40)
    private String invoiceId;

    @Column(name = "store_id", nullable = false, length = 20)
    private String storeId;

    @Column(name = "plan_id", length = 40)
    private String planId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "invoice_date", length = 20)
    private String date;
}
