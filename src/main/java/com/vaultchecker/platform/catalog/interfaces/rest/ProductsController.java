package com.vaultchecker.platform.catalog.interfaces.rest;

import com.vaultchecker.platform.catalog.application.commandservices.ProductCommandService;
import com.vaultchecker.platform.catalog.application.queryservices.ProductQueryService;
import com.vaultchecker.platform.catalog.domain.model.commands.DeleteProductCommand;
import com.vaultchecker.platform.catalog.domain.model.queries.GetAllProductsQuery;
import com.vaultchecker.platform.catalog.domain.model.queries.GetProductByIdQuery;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.CreateProductResource;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.ProductResource;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.UpdateProductResource;
import com.vaultchecker.platform.catalog.interfaces.rest.transform.CreateProductCommandFromResourceAssembler;
import com.vaultchecker.platform.catalog.interfaces.rest.transform.ProductResourceFromEntityAssembler;
import com.vaultchecker.platform.catalog.interfaces.rest.transform.UpdateProductCommandFromResourceAssembler;
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
 * REST controller exposing catalog product resources. Products can be filtered by store to keep
 * tenants isolated.
 */
@RestController
@RequestMapping(value = "/api/v1/products", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Products", description = "Store catalog (products/services) endpoints")
public class ProductsController {
    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    public ProductsController(ProductCommandService productCommandService, ProductQueryService productQueryService) {
        this.productCommandService = productCommandService;
        this.productQueryService = productQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all products (optionally filtered by storeId)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ProductResource>> getAllProducts(@RequestParam(required = false) String storeId) {
        var products = productQueryService.handle(new GetAllProductsQuery(storeId));
        return ResponseEntity.ok(products.stream().map(ProductResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductResource> getProductById(@PathVariable Long id) {
        return productQueryService.handle(new GetProductByIdQuery(id))
                .map(product -> ResponseEntity.ok(ProductResourceFromEntityAssembler.toResourceFromEntity(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Register a new product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductResource resource) {
        var command = CreateProductCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = productCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ProductResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductResource resource) {
        var command = UpdateProductCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = productCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ProductResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        var result = productCommandService.handle(new DeleteProductCommand(id));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, deleted -> null, HttpStatus.NO_CONTENT);
    }
}
