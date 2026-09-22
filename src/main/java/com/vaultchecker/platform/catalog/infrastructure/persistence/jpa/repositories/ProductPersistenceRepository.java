package com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.catalog.infrastructure.persistence.jpa.entities.ProductPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPersistenceRepository extends JpaRepository<ProductPersistenceEntity, Long> {
    List<ProductPersistenceEntity> findAllByStoreId(String storeId);
}
