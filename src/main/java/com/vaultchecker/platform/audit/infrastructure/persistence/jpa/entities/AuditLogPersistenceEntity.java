package com.vaultchecker.platform.audit.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "audit_id", length = 60)
    private String auditId;

    @Column(name = "user_role", length = 40)
    private String userRole;

    @Column(name = "action", nullable = false, length = 120)
    private String action;

    @Column(name = "log_date", length = 20)
    private String date;

    @Column(name = "log_time", length = 20)
    private String time;

    @Column(name = "details", length = 500)
    private String details;
}
