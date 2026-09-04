package com.vaultchecker.platform.stores.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.stores.infrastructure.persistence.jpa.entities.StorePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for store persistence entities.
 */
@Repository
public interface StorePersistenceRepository extends JpaRepository<StorePersistenceEntity, Long> {
    boolean existsByRuc(String ruc);

    boolean existsByStoreId(String storeId);
}
