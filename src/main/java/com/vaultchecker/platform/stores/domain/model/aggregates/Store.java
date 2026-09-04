package com.vaultchecker.platform.stores.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Store aggregate root. Represents a neighbourhood business (bodega, butcher, bakery, ...) that
 * operates on the platform. Deactivation is logical (US-02) so history is preserved; every product,
 * customer and credit account is scoped to a store to keep tenants isolated (US-03).
 */
@Getter
public class Store extends AbstractDomainAggregateRoot<Store> {

    @Setter
    private Long id;
    @Setter
    private String storeId;
    @Setter
    private String ruc;
    @Setter
    private String businessName;
    @Setter
    private String category;
    @Setter
    private String address;
    @Setter
    private String phone;
    @Setter
    private String email;
    @Setter
    private String description;
    @Setter
    private String state;

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";

    public Store() {
        this.state = ACTIVE;
    }

    public Store(String storeId, String ruc, String businessName, String category,
                 String address, String phone, String email, String description) {
        this.storeId = storeId;
        this.ruc = ruc;
        this.businessName = businessName;
        this.category = category;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.description = description;
        this.state = ACTIVE;
    }

    /** Logically deactivates the store without deleting its data. */
    public void deactivate() {
        this.state = INACTIVE;
    }

    /** Reactivates a previously deactivated store. */
    public void activate() {
        this.state = ACTIVE;
    }

    public boolean isActive() {
        return ACTIVE.equalsIgnoreCase(this.state);
    }
}
