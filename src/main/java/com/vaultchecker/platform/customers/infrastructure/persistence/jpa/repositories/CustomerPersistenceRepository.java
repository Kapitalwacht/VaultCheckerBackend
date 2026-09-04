package com.vaultchecker.platform.customers.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.customers.infrastructure.persistence.jpa.entities.CustomerPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for customer persistence entities.
 */
@Repository
public interface CustomerPersistenceRepository extends JpaRepository<CustomerPersistenceEntity, Long> {
    List<CustomerPersistenceEntity> findAllByStoreId(String storeId);
}
