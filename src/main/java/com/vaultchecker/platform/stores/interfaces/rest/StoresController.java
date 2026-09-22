package com.vaultchecker.platform.stores.interfaces.rest;

import com.vaultchecker.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vaultchecker.platform.stores.application.commandservices.StoreCommandService;
import com.vaultchecker.platform.stores.application.queryservices.StoreQueryService;
import com.vaultchecker.platform.stores.domain.model.commands.DeactivateStoreCommand;
import com.vaultchecker.platform.stores.domain.model.queries.GetAllStoresQuery;
import com.vaultchecker.platform.stores.domain.model.queries.GetStoreByIdQuery;
import com.vaultchecker.platform.stores.interfaces.rest.resources.CreateStoreResource;
import com.vaultchecker.platform.stores.interfaces.rest.resources.StoreResource;
import com.vaultchecker.platform.stores.interfaces.rest.resources.UpdateStoreResource;
import com.vaultchecker.platform.stores.interfaces.rest.transform.CreateStoreCommandFromResourceAssembler;
import com.vaultchecker.platform.stores.interfaces.rest.transform.StoreResourceFromEntityAssembler;
import com.vaultchecker.platform.stores.interfaces.rest.transform.UpdateStoreCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/stores", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Stores", description = "Platform store management endpoints")
public class StoresController {
    private final StoreCommandService storeCommandService;
    private final StoreQueryService storeQueryService;

    public StoresController(StoreCommandService storeCommandService, StoreQueryService storeQueryService) {
        this.storeCommandService = storeCommandService;
        this.storeQueryService = storeQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all stores", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<StoreResource>> getAllStores() {
        var stores = storeQueryService.handle(new GetAllStoresQuery());
        return ResponseEntity.ok(stores.stream().map(StoreResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get store by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<StoreResource> getStoreById(@PathVariable Long id) {
        return storeQueryService.handle(new GetStoreByIdQuery(id))
                .map(store -> ResponseEntity.ok(StoreResourceFromEntityAssembler.toResourceFromEntity(store)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Register a new store", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createStore(@Valid @RequestBody CreateStoreResource resource) {
        var command = CreateStoreCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = storeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, StoreResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a store", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateStore(@PathVariable Long id, @Valid @RequestBody UpdateStoreResource resource) {
        var command = UpdateStoreCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = storeCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, StoreResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a store (logical)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deactivateStore(@PathVariable Long id) {
        var result = storeCommandService.handle(new DeactivateStoreCommand(id));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, StoreResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
