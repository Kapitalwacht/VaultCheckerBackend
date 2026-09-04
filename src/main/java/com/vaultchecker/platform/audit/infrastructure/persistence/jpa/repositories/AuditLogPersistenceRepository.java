package com.vaultchecker.platform.audit.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.audit.infrastructure.persistence.jpa.entities.AuditLogPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for audit log persistence entities.
 */
@Repository
public interface AuditLogPersistenceRepository extends JpaRepository<AuditLogPersistenceEntity, Long> {
    List<AuditLogPersistenceEntity> findAllByDate(String date);
}
