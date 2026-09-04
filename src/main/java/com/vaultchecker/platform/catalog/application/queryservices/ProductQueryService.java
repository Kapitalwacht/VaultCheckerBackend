package com.vaultchecker.platform.catalog.application.queryservices;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.domain.model.queries.GetAllProductsQuery;
import com.vaultchecker.platform.catalog.domain.model.queries.GetProductByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for product read queries.
 */
public interface ProductQueryService {
    List<Product> handle(GetAllProductsQuery query);

    Optional<Product> handle(GetProductByIdQuery query);
}
