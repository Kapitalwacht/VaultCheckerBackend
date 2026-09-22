package com.vaultchecker.platform.audit.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;
import com.vaultchecker.platform.audit.infrastructure.persistence.jpa.entities.AuditLogPersistenceEntity;

public final class AuditLogPersistenceAssembler {

    private AuditLogPersistenceAssembler() {
    }

    public static AuditLog toDomainFromPersistence(AuditLogPersistenceEntity entity) {
        if (entity == null) return null;
        var log = new AuditLog();
        log.setId(entity.getId());
        log.setAuditId(entity.getAuditId());
        log.setUserRole(entity.getUserRole());
        log.setAction(entity.getAction());
        log.setDate(entity.getDate());
        log.setTime(entity.getTime());
        log.setDetails(entity.getDetails());
        return log;
    }

    public static AuditLogPersistenceEntity toPersistenceFromDomain(AuditLog log) {
        if (log == null) return null;
        var entity = new AuditLogPersistenceEntity();
        if (log.getId() != null) {
            entity.setId(log.getId());
        }
        entity.setAuditId(log.getAuditId());
        entity.setUserRole(log.getUserRole());
        entity.setAction(log.getAction());
        entity.setDate(log.getDate());
        entity.setTime(log.getTime());
        entity.setDetails(log.getDetails());
        return entity;
    }
}
