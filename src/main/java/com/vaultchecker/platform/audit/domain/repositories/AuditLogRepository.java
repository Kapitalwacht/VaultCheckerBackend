package com.vaultchecker.platform.audit.domain.repositories;

import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;

import java.util.List;
import java.util.Optional;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);

    List<AuditLog> findAll();

    List<AuditLog> findAllByDate(String date);

    Optional<AuditLog> findById(Long id);
}
