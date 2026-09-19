package com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.entities.ProductPersistenceEntity;

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
        product.setBrand(entity.getBrand());
        product.setUnit(entity.getUnit());
        product.setCashPrice(entity.getCashPrice());
        product.setListPrice(entity.getListPrice());
        product.setPaymentMode(entity.getPaymentMode());
        product.setImageUrl(entity.getImageUrl());
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
        entity.setBrand(product.getBrand());
        entity.setUnit(product.getUnit());
        entity.setCashPrice(product.getCashPrice());
        entity.setListPrice(product.getListPrice());
        entity.setPaymentMode(product.getPaymentMode());
        entity.setImageUrl(product.getImageUrl());
        entity.setState(product.getState());
        return entity;
    }
}
