package com.vaultchecker.platform.catalog.application.internal.queryservices;

import com.vaultchecker.platform.catalog.application.queryservices.ProductQueryService;
import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.domain.model.queries.GetAllProductsQuery;
import com.vaultchecker.platform.catalog.domain.model.queries.GetProductByIdQuery;
import com.vaultchecker.platform.catalog.domain.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    public ProductQueryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> handle(GetAllProductsQuery query) {
        if (query.storeId() != null && !query.storeId().isBlank()) {
            return productRepository.findAllByStoreId(query.storeId());
        }
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> handle(GetProductByIdQuery query) {
        return productRepository.findById(query.id());
    }
}
