package com.vaultchecker.platform.audit.application.queryservices;

import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;
import com.vaultchecker.platform.audit.domain.model.queries.GetAllAuditLogsQuery;
import com.vaultchecker.platform.audit.domain.model.queries.GetAuditLogByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for audit log read queries.
 */
public interface AuditLogQueryService {
    List<AuditLog> handle(GetAllAuditLogsQuery query);

    Optional<AuditLog> handle(GetAuditLogByIdQuery query);
}
