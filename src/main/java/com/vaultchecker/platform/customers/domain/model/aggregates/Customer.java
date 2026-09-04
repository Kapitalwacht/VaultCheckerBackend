package com.vaultchecker.platform.customers.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Customer aggregate root. A neighbour the store extends credit to. Scoped to a store
 * ({@code storeId}) for tenant isolation (US-03). Deactivation is logical (US-09).
 */
@Getter
public class Customer extends AbstractDomainAggregateRoot<Customer> {

    @Setter
    private Long id;
    @Setter
    private String customerId;
    @Setter
    private String storeId;
    @Setter
    private String firstName;
    @Setter
    private String lastName;
    @Setter
    private String dni;
    @Setter
    private String phone;
    @Setter
    private String address;
    @Setter
    private BigDecimal creditLimit;
    @Setter
    private String state;

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";

    public Customer() {
        this.state = ACTIVE;
        this.creditLimit = BigDecimal.ZERO;
    }

    public Customer(String customerId, String storeId, String firstName, String lastName,
                    String dni, String phone, String address, BigDecimal creditLimit) {
        this.customerId = customerId;
        this.storeId = storeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.phone = phone;
        this.address = address;
        this.creditLimit = creditLimit != null ? creditLimit : BigDecimal.ZERO;
        this.state = ACTIVE;
    }

    public void deactivate() {
        this.state = INACTIVE;
    }

    public boolean isActive() {
        return ACTIVE.equalsIgnoreCase(this.state);
    }
}
