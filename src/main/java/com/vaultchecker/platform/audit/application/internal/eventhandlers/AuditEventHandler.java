package com.vaultchecker.platform.audit.application.internal.eventhandlers;

import com.vaultchecker.platform.audit.application.commandservices.AuditLogCommandService;
import com.vaultchecker.platform.audit.domain.model.commands.RecordAuditLogCommand;
import com.vaultchecker.platform.iam.domain.model.events.UserSignedUpEvent;
import com.vaultchecker.platform.shared.domain.model.events.AuditableActionRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Listens for domain/integration events across the platform and persists an audit log, providing
 * traceability without coupling the emitting contexts to the audit persistence model (US-05).
 */
@Service
public class AuditEventHandler {

    private final AuditLogCommandService auditLogCommandService;

    public AuditEventHandler(AuditLogCommandService auditLogCommandService) {
        this.auditLogCommandService = auditLogCommandService;
    }

    /** Records any generic auditable action published through the shared event. */
    @EventListener
    public void on(AuditableActionRegisteredEvent event) {
        auditLogCommandService.handle(new RecordAuditLogCommand(event.userRole(), event.action(), event.details()));
    }

    /** Records new user registrations coming from the IAM context. */
    @EventListener
    public void on(UserSignedUpEvent event) {
        auditLogCommandService.handle(new RecordAuditLogCommand(
                event.role(),
                "USER_SIGNED_UP",
                "User %s (id=%d) registered".formatted(event.email(), event.userId())));
    }
}
