package com.vaultchecker.platform.catalog.interfaces.rest.transform;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.ProductResource;

public class ProductResourceFromEntityAssembler {
    public static ProductResource toResourceFromEntity(Product product) {
        return new ProductResource(product.getId(), product.getProductId(), product.getStoreId(), product.getName(),
                product.getCategory(), product.getBrand(), product.getUnit(), product.getCashPrice(),
                product.getListPrice(), product.getPaymentMode(), product.getImageUrl(), product.getState());
    }
}
