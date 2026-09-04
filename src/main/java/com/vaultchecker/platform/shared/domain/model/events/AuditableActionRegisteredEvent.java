package com.vaultchecker.platform.shared.domain.model.events;

/**
 * Shared integration event any bounded context can publish (through Spring's
 * {@code ApplicationEventPublisher}) to leave a trace of a relevant operation.
 *
 * <p>The {@code audit} bounded context listens to this event and persists it as an
 * {@code AuditLog}, giving the platform end-to-end traceability (User Story US-05)
 * without coupling the emitting context to the audit persistence model.</p>
 *
 * @param userRole the role of the user that performed the action (e.g. ROLE_STORE_ADMIN)
 * @param action   a short machine/human readable action label (e.g. "CREATE_STORE")
 * @param details  optional free-text details about the affected data
 */
public record AuditableActionRegisteredEvent(String userRole, String action, String details) {
}
