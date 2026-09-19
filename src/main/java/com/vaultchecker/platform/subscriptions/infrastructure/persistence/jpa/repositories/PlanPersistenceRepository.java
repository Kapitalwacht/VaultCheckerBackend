package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanPersistenceRepository extends JpaRepository<PlanPersistenceEntity, Long> {
    boolean existsByPlanId(String planId);
}
