package com.vaultchecker.platform.catalog.interfaces.rest.transform;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.ProductResource;

/**
 * Assembler that converts a {@link Product} aggregate into a {@link ProductResource}.
 */
public class ProductResourceFromEntityAssembler {
    public static ProductResource toResourceFromEntity(Product product) {
        return new ProductResource(product.getId(), product.getProductId(), product.getStoreId(), product.getName(),
                product.getCategory(), product.getUnit(), product.getPrice(), product.getStock(), product.getState());
    }
}
