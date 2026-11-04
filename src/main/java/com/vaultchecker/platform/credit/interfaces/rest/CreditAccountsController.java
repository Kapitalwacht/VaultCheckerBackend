package com.vaultchecker.platform.credit.interfaces.rest;

import com.vaultchecker.platform.credit.application.commandservices.CreditAccountCommandService;
import com.vaultchecker.platform.credit.application.queryservices.CreditAccountQueryService;
import com.vaultchecker.platform.credit.application.queryservices.CreditStatementQueryService;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllCreditAccountsQuery;
import com.vaultchecker.platform.credit.domain.model.queries.GetCreditAccountByIdQuery;
import com.vaultchecker.platform.credit.domain.model.queries.GetCreditStatementQuery;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreateCreditAccountResource;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreditAccountResource;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreditStatementResource;
import com.vaultchecker.platform.credit.interfaces.rest.resources.UpdateCreditAccountResource;
import com.vaultchecker.platform.credit.interfaces.rest.transform.CreateCreditAccountCommandFromResourceAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.CreditAccountResourceFromEntityAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.CreditStatementResourceFromEntityAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.UpdateCreditAccountCommandFromResourceAssembler;
import com.vaultchecker.platform.shared.infrastructure.security.CurrentUserScope;
import com.vaultchecker.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/credit-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Credit Accounts", description = "Customer credit account (cuenta corriente) endpoints")
public class CreditAccountsController {
    private final CreditAccountCommandService commandService;
    private final CreditAccountQueryService queryService;
    private final CreditStatementQueryService statementQueryService;

    public CreditAccountsController(CreditAccountCommandService commandService,
                                    CreditAccountQueryService queryService,
                                    CreditStatementQueryService statementQueryService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.statementQueryService = statementQueryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'STORE_ADMIN')")
    @Operation(summary = "Get all credit accounts (optionally filtered by storeId or customerId)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CreditAccountResource>> getAll(@RequestParam(required = false) String storeId,
                                                             @RequestParam(required = false) String customerId) {
        var accounts = queryService.handle(new GetAllCreditAccountsQuery(CurrentUserScope.restrictStore(storeId), customerId));
        return ResponseEntity.ok(accounts.stream().map(CreditAccountResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'STORE_ADMIN')")
    @Operation(summary = "Get credit account by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CreditAccountResource> getById(@PathVariable Long id) {
        return queryService.handle(new GetCreditAccountByIdQuery(id))
                .filter(account -> CurrentUserScope.canAccessStore(account.getStoreId()))
                .map(account -> ResponseEntity.ok(CreditAccountResourceFromEntityAssembler.toResourceFromEntity(account)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/statement")
    @Operation(summary = "Get a customer's cutoff-date statement (compensatory + moratory interest, US-21)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CreditStatementResource> getStatement(
            @RequestParam String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        CurrentUserScope.requireCustomer(customerId);
        return statementQueryService.handle(new GetCreditStatementQuery(customerId, asOfDate))
                .filter(statement -> CurrentUserScope.canAccessStore(statement.storeId()))
                .map(statement -> ResponseEntity.ok(CreditStatementResourceFromEntityAssembler.toResourceFromEntity(statement)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'STORE_ADMIN')")
    @Operation(summary = "Open a credit account", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> create(@Valid @RequestBody CreateCreditAccountResource resource) {
        CurrentUserScope.requireStore(resource.storeId());
        var command = CreateCreditAccountCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CreditAccountResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'STORE_ADMIN')")
    @Operation(summary = "Update a credit account", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateCreditAccountResource resource) {
        queryService.handle(new GetCreditAccountByIdQuery(id)).ifPresent(account -> CurrentUserScope.requireStore(account.getStoreId()));
        var command = UpdateCreditAccountCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CreditAccountResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'STORE_ADMIN')")
    @Operation(summary = "Delete a credit account", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> delete(@PathVariable Long id) {
        queryService.handle(new GetCreditAccountByIdQuery(id)).ifPresent(account -> CurrentUserScope.requireStore(account.getStoreId()));
        var result = commandService.handleDelete(id);
        return ResponseEntityAssembler.toResponseEntityFromResult(result, deleted -> null, HttpStatus.NO_CONTENT);
    }
}
