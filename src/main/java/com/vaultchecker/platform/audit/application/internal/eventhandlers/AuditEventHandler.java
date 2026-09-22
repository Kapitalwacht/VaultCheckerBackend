package com.vaultchecker.platform.audit.application.internal.eventhandlers;

import com.vaultchecker.platform.audit.application.commandservices.AuditLogCommandService;
import com.vaultchecker.platform.audit.domain.model.commands.RecordAuditLogCommand;
import com.vaultchecker.platform.iam.domain.model.events.UserSignedUpEvent;
import com.vaultchecker.platform.shared.domain.model.events.AuditableActionRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class AuditEventHandler {

    private final AuditLogCommandService auditLogCommandService;

    public AuditEventHandler(AuditLogCommandService auditLogCommandService) {
        this.auditLogCommandService = auditLogCommandService;
    }

    @EventListener
    public void on(AuditableActionRegisteredEvent event) {
        auditLogCommandService.handle(new RecordAuditLogCommand(event.userRole(), event.action(), event.details()));
    }

    @EventListener
    public void on(UserSignedUpEvent event) {
        auditLogCommandService.handle(new RecordAuditLogCommand(
                event.role(),
                "USER_SIGNED_UP",
                "User %s (id=%d) registered".formatted(event.email(), event.userId())));
    }
}
