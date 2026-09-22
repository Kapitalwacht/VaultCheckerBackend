package com.vaultchecker.platform.audit.interfaces.rest;

import com.vaultchecker.platform.audit.application.queryservices.AuditLogQueryService;
import com.vaultchecker.platform.audit.domain.model.queries.GetAllAuditLogsQuery;
import com.vaultchecker.platform.audit.domain.model.queries.GetAuditLogByIdQuery;
import com.vaultchecker.platform.audit.interfaces.rest.resources.AuditLogResource;
import com.vaultchecker.platform.audit.interfaces.rest.transform.AuditLogResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/audit-logs", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audit Logs", description = "Operation traceability endpoints")
public class AuditLogsController {
    private final AuditLogQueryService auditLogQueryService;

    public AuditLogsController(AuditLogQueryService auditLogQueryService) {
        this.auditLogQueryService = auditLogQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all audit logs (optionally filtered by date)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<AuditLogResource>> getAll(@RequestParam(required = false) String date) {
        var logs = auditLogQueryService.handle(new GetAllAuditLogsQuery(date));
        return ResponseEntity.ok(logs.stream().map(AuditLogResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<AuditLogResource> getById(@PathVariable Long id) {
        return auditLogQueryService.handle(new GetAuditLogByIdQuery(id))
                .map(log -> ResponseEntity.ok(AuditLogResourceFromEntityAssembler.toResourceFromEntity(log)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
