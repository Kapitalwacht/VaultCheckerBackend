package com.vaultchecker.platform.catalog.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Product aggregate root. A product/service a store offers on credit. Scoped to a store
 * ({@code storeId}) for tenant isolation. Carries cash and list (credit) prices and the
 * accepted payment mode. Deactivation is logical.
 */
@Getter
public class Product extends AbstractDomainAggregateRoot<Product> {

    @Setter private Long id;
    @Setter private String productId;
    @Setter private String storeId;
    @Setter private String name;
    @Setter private String category;
    @Setter private String brand;
    @Setter private String unit;
    @Setter private BigDecimal cashPrice;
    @Setter private BigDecimal listPrice;
    @Setter private String paymentMode;
    @Setter private String imageUrl;
    @Setter private String state;

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";
    public static final String BOTH = "both";

    public Product() {
        this.state = ACTIVE;
        this.cashPrice = BigDecimal.ZERO;
        this.listPrice = BigDecimal.ZERO;
        this.unit = "unit";
        this.paymentMode = BOTH;
    }

    public Product(String productId, String storeId, String name, String category, String brand,
                   String unit, BigDecimal cashPrice, BigDecimal listPrice, String paymentMode, String imageUrl) {
        this();
        this.productId = productId;
        this.storeId = storeId;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.unit = unit != null ? unit : "unit";
        this.cashPrice = cashPrice != null ? cashPrice : BigDecimal.ZERO;
        this.listPrice = listPrice != null ? listPrice : BigDecimal.ZERO;
        this.paymentMode = paymentMode != null ? paymentMode : BOTH;
        this.imageUrl = imageUrl;
    }

    public void deactivate() {
        this.state = INACTIVE;
    }

    public boolean isActive() {
        return ACTIVE.equalsIgnoreCase(this.state);
    }
}
