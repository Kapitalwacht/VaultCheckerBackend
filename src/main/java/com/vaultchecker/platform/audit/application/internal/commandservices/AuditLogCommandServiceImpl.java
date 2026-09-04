package com.vaultchecker.platform.audit.application.internal.commandservices;

import com.vaultchecker.platform.audit.application.commandservices.AuditLogCommandService;
import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;
import com.vaultchecker.platform.audit.domain.model.commands.RecordAuditLogCommand;
import com.vaultchecker.platform.audit.domain.repositories.AuditLogRepository;
import org.springframework.stereotype.Service;

/**
 * Audit log command service implementation.
 */
@Service
public class AuditLogCommandServiceImpl implements AuditLogCommandService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogCommandServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLog handle(RecordAuditLogCommand command) {
        var auditLog = new AuditLog(command.userRole(), command.action(), command.details());
        return auditLogRepository.save(auditLog);
    }
}
