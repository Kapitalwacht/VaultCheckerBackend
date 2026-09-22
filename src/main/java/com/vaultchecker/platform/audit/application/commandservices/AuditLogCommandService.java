package com.vaultchecker.platform.audit.application.commandservices;

import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;
import com.vaultchecker.platform.audit.domain.model.commands.RecordAuditLogCommand;

public interface AuditLogCommandService {
    AuditLog handle(RecordAuditLogCommand command);
}
