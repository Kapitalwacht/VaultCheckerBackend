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
 * JPA persistence entity for credit accounts.
 */
@Entity
@Table(name = "credit_accounts")
@Getter
@Setter
@NoArgsConstructor
public class CreditAccountPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "credit_id", length = 60)
    private String creditId;

    @Column(name = "store_id", length = 60)
    private String storeId;

    @Column(name = "customer_id", length = 60)
    private String customerId;

    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "state", nullable = false, length = 20)
    private String state;
}
