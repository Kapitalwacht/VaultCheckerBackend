package com.vaultchecker.platform.credit.interfaces.rest;

import com.vaultchecker.platform.credit.application.commandservices.CreditAccountCommandService;
import com.vaultchecker.platform.credit.application.queryservices.CreditAccountQueryService;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllCreditAccountsQuery;
import com.vaultchecker.platform.credit.domain.model.queries.GetCreditAccountByIdQuery;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreateCreditAccountResource;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreditAccountResource;
import com.vaultchecker.platform.credit.interfaces.rest.resources.UpdateCreditAccountResource;
import com.vaultchecker.platform.credit.interfaces.rest.transform.CreateCreditAccountCommandFromResourceAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.CreditAccountResourceFromEntityAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.UpdateCreditAccountCommandFromResourceAssembler;
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

/**
 * REST controller exposing credit account resources. Accounts can be filtered by store or customer
 * (a customer sees only their own account).
 */
@RestController
@RequestMapping(value = "/api/v1/credit-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Credit Accounts", description = "Customer credit account (cuenta corriente) endpoints")
public class CreditAccountsController {
    private final CreditAccountCommandService commandService;
    private final CreditAccountQueryService queryService;

    public CreditAccountsController(CreditAccountCommandService commandService, CreditAccountQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "Get all credit accounts (optionally filtered by storeId or customerId)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CreditAccountResource>> getAll(@RequestParam(required = false) String storeId,
                                                             @RequestParam(required = false) String customerId) {
        var accounts = queryService.handle(new GetAllCreditAccountsQuery(storeId, customerId));
        return ResponseEntity.ok(accounts.stream().map(CreditAccountResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get credit account by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CreditAccountResource> getById(@PathVariable Long id) {
        return queryService.handle(new GetCreditAccountByIdQuery(id))
                .map(account -> ResponseEntity.ok(CreditAccountResourceFromEntityAssembler.toResourceFromEntity(account)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Open a credit account", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> create(@Valid @RequestBody CreateCreditAccountResource resource) {
        var command = CreateCreditAccountCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CreditAccountResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a credit account", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateCreditAccountResource resource) {
        var command = UpdateCreditAccountCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CreditAccountResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a credit account", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> delete(@PathVariable Long id) {
        var result = commandService.handleDelete(id);
        return ResponseEntityAssembler.toResponseEntityFromResult(result, deleted -> null, HttpStatus.NO_CONTENT);
    }
}
