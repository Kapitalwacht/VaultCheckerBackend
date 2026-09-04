package com.vaultchecker.platform.iam.domain.model.events;

/**
 * Domain event published when a new user successfully signs up. Consumed by the audit
 * context to leave a traceability record.
 *
 * @param userId the identifier of the newly created user
 * @param email  the email of the newly created user
 * @param role   the primary role assigned at registration time
 */
public record UserSignedUpEvent(Long userId, String email, String role) {
}
