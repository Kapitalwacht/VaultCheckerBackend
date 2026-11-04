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
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
public class PurchasePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "purchase_id", length = 60)
    private String purchaseId;

    @Column(name = "store_id", length = 60)
    private String storeId;

    @Column(name = "customer_id", length = 60)
    private String customerId;

    @Column(name = "product_id", length = 60)
    private String productId;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "months")
    private Integer months;

    @Column(name = "state", nullable = false, length = 20)
    private String state;

    @Column(name = "rate_type", length = 20)
    private String rateType;

    @Column(name = "rate_value", precision = 12, scale = 7)
    private BigDecimal rateValue;

    @Column(name = "rate_period_days")
    private Integer ratePeriodDays;

    @Column(name = "rate_capitalization_days")
    private Integer rateCapitalizationDays;

    @Column(name = "moratorium_rate_type", length = 20)
    private String moratoriumRateType;

    @Column(name = "moratorium_rate_value", precision = 12, scale = 7)
    private BigDecimal moratoriumRateValue;

    @Column(name = "cutoff_day")
    private Integer cutoffDay;

    @Column(name = "payment_day")
    private Integer paymentDay;
}
