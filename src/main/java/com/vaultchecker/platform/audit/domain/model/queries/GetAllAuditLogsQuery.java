package com.vaultchecker.platform.audit.domain.model.queries;

/**
 * Query to get all audit logs, optionally filtered by date.
 *
 * @param date optional date filter in ISO format (yyyy-MM-dd); {@code null} returns all logs
 */
public record GetAllAuditLogsQuery(String date) {
}
