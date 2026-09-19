package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities.PurchasePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasePersistenceRepository extends JpaRepository<PurchasePersistenceEntity, Long> {
    List<PurchasePersistenceEntity> findAllByStoreId(String storeId);

    List<PurchasePersistenceEntity> findAllByCustomerId(String customerId);
}
