package com.vaultchecker.platform.audit.domain.model.commands;

/**
 * Command to record an audit log entry (US-05).
 */
public record RecordAuditLogCommand(String userRole, String action, String details) {
}
