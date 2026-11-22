package com.vaultchecker.platform.shared.application.audit;

import com.vaultchecker.platform.shared.domain.model.events.AuditableActionRegisteredEvent;
import com.vaultchecker.platform.shared.infrastructure.security.CurrentUserRole;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Records an operation in the audit log on behalf of the authenticated user.
 * Command services call it after an operation is saved, so every change is audited by the backend.
 */
@Component
public class AuditTrail {

    private final ApplicationEventPublisher eventPublisher;

    public AuditTrail(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void record(String action, String details, String storeId) {
        eventPublisher.publishEvent(new AuditableActionRegisteredEvent(
                CurrentUserRole.clientRole(), action, details, storeId));
    }
}
