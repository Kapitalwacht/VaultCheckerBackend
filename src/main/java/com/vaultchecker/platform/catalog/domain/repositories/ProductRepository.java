package com.vaultchecker.platform.catalog.domain.repositories;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);

    List<Product> findAll();

    List<Product> findAllByStoreId(String storeId);

    Product save(Product product);

    void deleteById(Long id);

    boolean existsById(Long id);
}
