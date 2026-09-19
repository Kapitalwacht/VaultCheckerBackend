package com.vaultchecker.platform.customers.interfaces.rest;

import com.vaultchecker.platform.customers.application.commandservices.CustomerCommandService;
import com.vaultchecker.platform.customers.application.queryservices.CustomerQueryService;
import com.vaultchecker.platform.customers.domain.model.commands.DeleteCustomerCommand;
import com.vaultchecker.platform.customers.domain.model.queries.GetAllCustomersQuery;
import com.vaultchecker.platform.customers.domain.model.queries.GetCustomerByIdQuery;
import com.vaultchecker.platform.customers.interfaces.rest.resources.CreateCustomerResource;
import com.vaultchecker.platform.customers.interfaces.rest.resources.CustomerResource;
import com.vaultchecker.platform.customers.interfaces.rest.resources.UpdateCustomerResource;
import com.vaultchecker.platform.customers.interfaces.rest.transform.CreateCustomerCommandFromResourceAssembler;
import com.vaultchecker.platform.customers.interfaces.rest.transform.CustomerResourceFromEntityAssembler;
import com.vaultchecker.platform.customers.interfaces.rest.transform.UpdateCustomerCommandFromResourceAssembler;
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
 * REST controller exposing customer resources. Customers can be filtered by store.
 */
@RestController
@RequestMapping(value = "/api/v1/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Customers", description = "Store customer management endpoints")
public class CustomersController {
    private final CustomerCommandService customerCommandService;
    private final CustomerQueryService customerQueryService;

    public CustomersController(CustomerCommandService customerCommandService, CustomerQueryService customerQueryService) {
        this.customerCommandService = customerCommandService;
        this.customerQueryService = customerQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all customers (optionally filtered by storeId)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CustomerResource>> getAllCustomers(@RequestParam(required = false) String storeId) {
        var customers = customerQueryService.handle(new GetAllCustomersQuery(storeId));
        return ResponseEntity.ok(customers.stream().map(CustomerResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomerResource> getCustomerById(@PathVariable Long id) {
        return customerQueryService.handle(new GetCustomerByIdQuery(id))
                .map(customer -> ResponseEntity.ok(CustomerResourceFromEntityAssembler.toResourceFromEntity(customer)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Register a new customer", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CreateCustomerResource resource) {
        var command = CreateCustomerCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = customerCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CustomerResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody UpdateCustomerResource resource) {
        var command = UpdateCustomerCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = customerCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, CustomerResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        var result = customerCommandService.handle(new DeleteCustomerCommand(id));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, deleted -> null, HttpStatus.NO_CONTENT);
    }
}
