package com.vaultchecker.platform.audit.interfaces.rest.resources;

public record AuditLogResource(Long id, String auditId, String userRole, String action,
                               String date, String time, String details) {
}
