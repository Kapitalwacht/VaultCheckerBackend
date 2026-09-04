package com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.entities.ProductPersistenceEntity;

/**
 * Static assembler between product domain and persistence representations.
 */
public final class ProductPersistenceAssembler {

    private ProductPersistenceAssembler() {
    }

    public static Product toDomainFromPersistence(ProductPersistenceEntity entity) {
        if (entity == null) return null;
        var product = new Product();
        product.setId(entity.getId());
        product.setProductId(entity.getProductId());
        product.setStoreId(entity.getStoreId());
        product.setName(entity.getName());
        product.setCategory(entity.getCategory());
        product.setUnit(entity.getUnit());
        product.setPrice(entity.getPrice());
        product.setStock(entity.getStock());
        product.setState(entity.getState());
        return product;
    }

    public static ProductPersistenceEntity toPersistenceFromDomain(Product product) {
        if (product == null) return null;
        var entity = new ProductPersistenceEntity();
        if (product.getId() != null) {
            entity.setId(product.getId());
        }
        entity.setProductId(product.getProductId());
        entity.setStoreId(product.getStoreId());
        entity.setName(product.getName());
        entity.setCategory(product.getCategory());
        entity.setUnit(product.getUnit());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setState(product.getState());
        return entity;
    }
}
