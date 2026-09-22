package com.vaultchecker.platform.shared.domain.model.events;

public record AuditableActionRegisteredEvent(String userRole, String action, String details) {
}
