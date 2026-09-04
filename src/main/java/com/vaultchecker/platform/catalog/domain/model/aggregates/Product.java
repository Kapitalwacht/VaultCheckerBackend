package com.vaultchecker.platform.catalog.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Product aggregate root. A product/service a store offers on credit. Scoped to a store
 * ({@code storeId}) for tenant isolation (US-03). Deactivation is logical (US-06).
 */
@Getter
public class Product extends AbstractDomainAggregateRoot<Product> {

    @Setter
    private Long id;
    @Setter
    private String productId;
    @Setter
    private String storeId;
    @Setter
    private String name;
    @Setter
    private String category;
    @Setter
    private String unit;
    @Setter
    private BigDecimal price;
    @Setter
    private Integer stock;
    @Setter
    private String state;

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";

    public Product() {
        this.state = ACTIVE;
        this.price = BigDecimal.ZERO;
        this.stock = 0;
        this.unit = "unit";
    }

    public Product(String productId, String storeId, String name, String category,
                   String unit, BigDecimal price, Integer stock) {
        this.productId = productId;
        this.storeId = storeId;
        this.name = name;
        this.category = category;
        this.unit = unit != null ? unit : "unit";
        this.price = price != null ? price : BigDecimal.ZERO;
        this.stock = stock != null ? stock : 0;
        this.state = ACTIVE;
    }

    public void deactivate() {
        this.state = INACTIVE;
    }

    public boolean isActive() {
        return ACTIVE.equalsIgnoreCase(this.state);
    }
}
