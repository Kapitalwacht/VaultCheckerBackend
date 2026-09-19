package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities.InstallmentPaymentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentPaymentPersistenceRepository extends JpaRepository<InstallmentPaymentPersistenceEntity, Long> {
    List<InstallmentPaymentPersistenceEntity> findAllByStoreId(String storeId);

    List<InstallmentPaymentPersistenceEntity> findAllByCustomerId(String customerId);

    List<InstallmentPaymentPersistenceEntity> findAllByPurchaseId(String purchaseId);
}
