package com.vaultchecker.platform.audit.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RecordAuditLogRequest", description = "Action performed by the signed-in user")
public record RecordAuditLogResource(
        @Schema(description = "Role of the user performing the action", example = "store-admin")
        @Size(max = 40)
        String userRole,
        @Schema(description = "Action code", example = "REGISTER_PURCHASE")
        @NotBlank @Size(max = 60)
        String action,
        @Schema(description = "Human readable details", example = "Purchase of Arroz 5kg for 100.00 (3 months)")
        @Size(max = 500)
        String details
) {
}
