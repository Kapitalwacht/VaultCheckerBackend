package com.vaultchecker.platform.audit.interfaces.rest.transform;

import com.vaultchecker.platform.audit.domain.model.aggregates.AuditLog;
import com.vaultchecker.platform.audit.interfaces.rest.resources.AuditLogResource;

public class AuditLogResourceFromEntityAssembler {
    public static AuditLogResource toResourceFromEntity(AuditLog log) {
        return new AuditLogResource(log.getId(), log.getAuditId(), log.getUserRole(), log.getAction(),
                log.getDate(), log.getTime(), log.getDetails());
    }
}
