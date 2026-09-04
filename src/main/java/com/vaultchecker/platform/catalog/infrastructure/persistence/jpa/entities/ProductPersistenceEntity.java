package com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA persistence entity for products.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "product_id", length = 60)
    private String productId;

    @Column(name = "store_id", length = 60)
    private String storeId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "category", length = 80)
    private String category;

    @Column(name = "unit", length = 30)
    private String unit;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "state", nullable = false, length = 20)
    private String state;
}
