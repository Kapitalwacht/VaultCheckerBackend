package com.vaultchecker.platform.audit.application.internal.queryservices;

import com.vaultchecker.platform.audit.application.queryservices.AuditLogQueryService;
import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;
import com.vaultchecker.platform.audit.domain.model.queries.GetAllAuditLogsQuery;
import com.vaultchecker.platform.audit.domain.model.queries.GetAuditLogByIdQuery;
import com.vaultchecker.platform.audit.domain.repositories.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Audit log query service implementation.
 */
@Service
public class AuditLogQueryServiceImpl implements AuditLogQueryService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogQueryServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<AuditLog> handle(GetAllAuditLogsQuery query) {
        if (query.date() != null && !query.date().isBlank()) {
            return auditLogRepository.findAllByDate(query.date());
        }
        return auditLogRepository.findAll();
    }

    @Override
    public Optional<AuditLog> handle(GetAuditLogByIdQuery query) {
        return auditLogRepository.findById(query.id());
    }
}
