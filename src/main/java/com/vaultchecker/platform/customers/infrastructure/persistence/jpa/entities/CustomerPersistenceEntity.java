package com.vaultchecker.platform.customers.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class CustomerPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "customer_id", length = 60)
    private String customerId;

    @Column(name = "store_id", length = 60)
    private String storeId;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "dni", length = 20)
    private String dni;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "rate_type", length = 20)
    private String rateType;

    @Column(name = "rate_value", precision = 12, scale = 9)
    private BigDecimal rateValue;

    @Column(name = "rate_capitalization_days")
    private Integer rateCapitalizationDays;

    @Column(name = "rate_period_days")
    private Integer ratePeriodDays;

    @Column(name = "moratorium_rate_type", length = 20)
    private String moratoriumRateType;

    @Column(name = "moratorium_rate_value", precision = 12, scale = 9)
    private BigDecimal moratoriumRateValue;

    @Column(name = "max_months")
    private Integer maxMonths;

    @Column(name = "cutoff_day")
    private Integer cutoffDay;

    @Column(name = "payment_day")
    private Integer paymentDay;

    @Column(name = "state", nullable = false, length = 20)
    private String state;
}
