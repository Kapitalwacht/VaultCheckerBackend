package com.vaultchecker.platform.audit.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;
import com.vaultchecker.platform.audit.domain.repositories.AuditLogRepository;
import com.vaultchecker.platform.audit.infrastructure.persistence.jpa.assemblers.AuditLogPersistenceAssembler;
import com.vaultchecker.platform.audit.infrastructure.persistence.jpa.repositories.AuditLogPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogPersistenceRepository repository;

    public AuditLogRepositoryImpl(AuditLogPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        var saved = repository.save(AuditLogPersistenceAssembler.toPersistenceFromDomain(auditLog));
        return AuditLogPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public List<AuditLog> findAll() {
        return repository.findAll().stream().map(AuditLogPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<AuditLog> findAllByDate(String date) {
        return repository.findAllByDate(date).stream().map(AuditLogPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Optional<AuditLog> findById(Long id) {
        return repository.findById(id).map(AuditLogPersistenceAssembler::toDomainFromPersistence);
    }
}
