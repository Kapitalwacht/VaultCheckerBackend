package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanPersistenceRepository extends JpaRepository<PlanPersistenceEntity, Long> {
    boolean existsByPlanId(String planId);

    Optional<PlanPersistenceEntity> findByPlanId(String planId);
}
