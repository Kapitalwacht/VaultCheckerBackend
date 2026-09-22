package com.vaultchecker.platform.audit.domain.model.commands;

public record RecordAuditLogCommand(String userRole, String action, String details) {
}
