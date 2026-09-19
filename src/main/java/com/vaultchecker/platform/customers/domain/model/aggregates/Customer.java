package com.vaultchecker.platform.customers.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Customer aggregate root. A neighbour the store extends credit to. Scoped to a store
 * ({@code storeId}) for tenant isolation. Carries the agreed credit terms (rate, currency,
 * cutoff/payment calendar). Deactivation is logical.
 */
@Getter
public class Customer extends AbstractDomainAggregateRoot<Customer> {

    @Setter private Long id;
    @Setter private String customerId;
    @Setter private String storeId;
    @Setter private String firstName;
    @Setter private String lastName;
    @Setter private String dni;
    @Setter private String phone;
    @Setter private String address;
    @Setter private BigDecimal creditLimit;
    @Setter private String currency;
    @Setter private String rateType;
    @Setter private BigDecimal rateValue;
    @Setter private Integer rateCapitalizationDays;
    @Setter private Integer ratePeriodDays;
    @Setter private String moratoriumRateType;
    @Setter private BigDecimal moratoriumRateValue;
    @Setter private Integer maxMonths;
    @Setter private Integer cutoffDay;
    @Setter private Integer paymentDay;
    @Setter private String state;

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";
    public static final String EFFECTIVE = "effective";

    public Customer() {
        this.state = ACTIVE;
        this.creditLimit = BigDecimal.ZERO;
        this.currency = "PEN";
        this.rateType = EFFECTIVE;
        this.rateValue = BigDecimal.ZERO;
        this.rateCapitalizationDays = 30;
        this.ratePeriodDays = 360;
        this.moratoriumRateType = EFFECTIVE;
        this.moratoriumRateValue = BigDecimal.ZERO;
        this.maxMonths = 1;
        this.cutoffDay = 1;
        this.paymentDay = 1;
    }

    public Customer(String customerId, String storeId, String firstName, String lastName,
                    String dni, String phone, String address, BigDecimal creditLimit) {
        this();
        this.customerId = customerId;
        this.storeId = storeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.phone = phone;
        this.address = address;
        this.creditLimit = creditLimit != null ? creditLimit : BigDecimal.ZERO;
    }

    public void deactivate() {
        this.state = INACTIVE;
    }

    public boolean isActive() {
        return ACTIVE.equalsIgnoreCase(this.state);
    }
}
