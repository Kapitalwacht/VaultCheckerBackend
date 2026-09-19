package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "installment_payments")
@Getter
@Setter
@NoArgsConstructor
public class InstallmentPaymentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "payment_id", length = 60)
    private String paymentId;

    @Column(name = "store_id", length = 60)
    private String storeId;

    @Column(name = "customer_id", length = 60)
    private String customerId;

    @Column(name = "purchase_id", length = 60)
    private String purchaseId;

    @Column(name = "period")
    private Integer period;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "installment", precision = 15, scale = 2)
    private BigDecimal installment;

    @Column(name = "late_fee", precision = 15, scale = 2)
    private BigDecimal lateFee;

    @Column(name = "interest", precision = 15, scale = 2)
    private BigDecimal interest;

    @Column(name = "principal", precision = 15, scale = 2)
    private BigDecimal principal;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total;

    @Column(name = "state", nullable = false, length = 20)
    private String state;
}
