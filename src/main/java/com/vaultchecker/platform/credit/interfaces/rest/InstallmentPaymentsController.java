package com.vaultchecker.platform.credit.interfaces.rest;

import com.vaultchecker.platform.credit.application.commandservices.InstallmentPaymentCommandService;
import com.vaultchecker.platform.credit.application.queryservices.InstallmentPaymentQueryService;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllInstallmentPaymentsQuery;
import com.vaultchecker.platform.credit.interfaces.rest.resources.InstallmentPaymentResource;
import com.vaultchecker.platform.credit.interfaces.rest.resources.RegisterInstallmentPaymentResource;
import com.vaultchecker.platform.credit.interfaces.rest.transform.InstallmentPaymentResourceFromEntityAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.RegisterInstallmentPaymentCommandFromResourceAssembler;
import com.vaultchecker.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/installment-payments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Installment Payments", description = "Installment payment endpoints")
public class InstallmentPaymentsController {
    private final InstallmentPaymentCommandService commandService;
    private final InstallmentPaymentQueryService queryService;

    public InstallmentPaymentsController(InstallmentPaymentCommandService commandService,
                                         InstallmentPaymentQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "Get all installment payments (optionally filtered)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<InstallmentPaymentResource>> getAll(@RequestParam(required = false) String storeId,
                                                                   @RequestParam(required = false) String customerId,
                                                                   @RequestParam(required = false) String purchaseId) {
        var payments = queryService.handle(new GetAllInstallmentPaymentsQuery(storeId, customerId, purchaseId));
        return ResponseEntity.ok(payments.stream()
                .map(InstallmentPaymentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @PostMapping
    @Operation(summary = "Register an installment payment", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> create(@Valid @RequestBody RegisterInstallmentPaymentResource resource) {
        var command = RegisterInstallmentPaymentCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, InstallmentPaymentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
