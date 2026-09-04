package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities.CreditAccountPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for credit account persistence entities.
 */
@Repository
public interface CreditAccountPersistenceRepository extends JpaRepository<CreditAccountPersistenceEntity, Long> {
    List<CreditAccountPersistenceEntity> findAllByStoreId(String storeId);

    List<CreditAccountPersistenceEntity> findAllByCustomerId(String customerId);
}
