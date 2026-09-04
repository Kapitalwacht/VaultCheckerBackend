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

/**
 * JPA persistence entity for payments.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class PaymentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "credit_account_id")
    private Long creditAccountId;

    @Column(name = "store_id", length = 60)
    private String storeId;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date")
    private LocalDate date;

    @Column(name = "applied_to_late_interest", precision = 15, scale = 2)
    private BigDecimal appliedToLateInterest;

    @Column(name = "applied_to_compensatory_interest", precision = 15, scale = 2)
    private BigDecimal appliedToCompensatoryInterest;

    @Column(name = "applied_to_principal", precision = 15, scale = 2)
    private BigDecimal appliedToPrincipal;
}
