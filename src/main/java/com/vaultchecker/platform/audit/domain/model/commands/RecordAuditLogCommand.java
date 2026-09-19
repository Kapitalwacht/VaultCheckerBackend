package com.vaultchecker.platform.audit.domain.model.commands;

/**
 * Command to record an audit log entry.
 */
public record RecordAuditLogCommand(String userRole, String action, String details) {
}
