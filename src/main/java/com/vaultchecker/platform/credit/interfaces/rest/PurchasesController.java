package com.vaultchecker.platform.credit.interfaces.rest;

import com.vaultchecker.platform.credit.application.commandservices.PurchaseCommandService;
import com.vaultchecker.platform.credit.application.queryservices.PurchaseQueryService;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllPurchasesQuery;
import com.vaultchecker.platform.credit.interfaces.rest.resources.PurchaseResource;
import com.vaultchecker.platform.credit.interfaces.rest.resources.RegisterPurchaseResource;
import com.vaultchecker.platform.credit.interfaces.rest.transform.PurchaseResourceFromEntityAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.RegisterPurchaseCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/purchases", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Purchases", description = "Credit purchase endpoints")
public class PurchasesController {
    private final PurchaseCommandService commandService;
    private final PurchaseQueryService queryService;

    public PurchasesController(PurchaseCommandService commandService, PurchaseQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "Get all purchases (optionally filtered by storeId or customerId)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<PurchaseResource>> getAll(@RequestParam(required = false) String storeId,
                                                         @RequestParam(required = false) String customerId) {
        var purchases = queryService.handle(new GetAllPurchasesQuery(storeId, customerId));
        return ResponseEntity.ok(purchases.stream()
                .map(PurchaseResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @PostMapping
    @Operation(summary = "Register a purchase", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> create(@Valid @RequestBody RegisterPurchaseResource resource) {
        var command = RegisterPurchaseCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, PurchaseResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
