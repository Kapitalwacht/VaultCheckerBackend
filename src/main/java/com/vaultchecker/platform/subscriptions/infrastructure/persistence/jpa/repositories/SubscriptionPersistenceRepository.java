package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPersistenceRepository extends JpaRepository<SubscriptionPersistenceEntity, Long> {
    Optional<SubscriptionPersistenceEntity> findByStoreId(String storeId);

    @Modifying
    @Transactional
    @Query("update SubscriptionPersistenceEntity e set e.planId = :newPlanId where e.planId = :oldPlanId")
    int renamePlanId(@Param("oldPlanId") String oldPlanId, @Param("newPlanId") String newPlanId);
}
