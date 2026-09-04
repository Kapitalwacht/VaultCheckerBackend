package com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.domain.repositories.ProductRepository;
import com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.assemblers.ProductPersistenceAssembler;
import com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.repositories.ProductPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the product domain repository port with Spring Data JPA.
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductPersistenceRepository productPersistenceRepository;

    public ProductRepositoryImpl(ProductPersistenceRepository productPersistenceRepository) {
        this.productPersistenceRepository = productPersistenceRepository;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productPersistenceRepository.findById(id).map(ProductPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Product> findAll() {
        return productPersistenceRepository.findAll().stream().map(ProductPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Product> findAllByStoreId(String storeId) {
        return productPersistenceRepository.findAllByStoreId(storeId).stream().map(ProductPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Product save(Product product) {
        var saved = productPersistenceRepository.save(ProductPersistenceAssembler.toPersistenceFromDomain(product));
        return ProductPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public void deleteById(Long id) {
        productPersistenceRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productPersistenceRepository.existsById(id);
    }
}
