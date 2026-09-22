package com.vaultchecker.platform.stores.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

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

    public void deactivate() {
        this.state = INACTIVE;
    }

    public void activate() {
        this.state = ACTIVE;
    }

    public boolean isActive() {
        return ACTIVE.equalsIgnoreCase(this.state);
    }
}
