package com.vaultchecker.platform.audit.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class AuditLog extends AbstractDomainAggregateRoot<AuditLog> {

    @Setter
    private Long id;
    @Setter
    private String auditId;
    @Setter
    private String userRole;
    @Setter
    private String action;
    @Setter
    private String date;
    @Setter
    private String time;
    @Setter
    private String details;

    public AuditLog() {
    }

    public AuditLog(String userRole, String action, String details) {
        this.userRole = userRole;
        this.action = action;
        this.details = details;
        this.date = LocalDate.now().toString();
        this.time = LocalTime.now().withNano(0).toString();
    }
}
