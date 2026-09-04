package com.vaultchecker.platform.audit.interfaces.rest.resources;

/**
 * Resource representing an audit log entry returned by the REST API. Field names match the frontend.
 */
public record AuditLogResource(Long id, String auditId, String userRole, String action,
                               String date, String time, String details) {
}
