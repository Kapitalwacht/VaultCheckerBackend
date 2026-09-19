package com.vaultchecker.platform.subscriptions.interfaces.rest;

import com.vaultchecker.platform.subscriptions.application.commandservices.BillingCommandService;
import com.vaultchecker.platform.subscriptions.application.queryservices.BillingQueryService;
import com.vaultchecker.platform.subscriptions.domain.model.commands.ChangePlanCommand;
import com.vaultchecker.platform.subscriptions.interfaces.rest.resources.ChangePlanResource;
import com.vaultchecker.platform.subscriptions.interfaces.rest.resources.InvoiceResource;
import com.vaultchecker.platform.subscriptions.interfaces.rest.resources.PlanResource;
import com.vaultchecker.platform.subscriptions.interfaces.rest.resources.SubscriptionResource;
import com.vaultchecker.platform.subscriptions.interfaces.rest.transform.InvoiceResourceFromEntityAssembler;
import com.vaultchecker.platform.subscriptions.interfaces.rest.transform.PlanResourceFromEntityAssembler;
import com.vaultchecker.platform.subscriptions.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Billing", description = "Subscription plans, subscriptions and invoices endpoints")
public class BillingController {

    private final BillingQueryService billingQueryService;
    private final BillingCommandService billingCommandService;

    public BillingController(BillingQueryService billingQueryService, BillingCommandService billingCommandService) {
        this.billingQueryService = billingQueryService;
        this.billingCommandService = billingCommandService;
    }

    @GetMapping("/plans")
    @Operation(summary = "Get all subscription plans", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<PlanResource>> getPlans() {
        var plans = billingQueryService.getAllPlans().stream()
                .map(PlanResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/subscriptions")
    @Operation(summary = "Get the subscription of a store", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<SubscriptionResource>> getSubscriptions(@RequestParam(required = false) String storeId) {
        if (storeId == null || storeId.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        var resources = billingQueryService.getSubscriptionByStore(storeId)
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .map(List::of)
                .orElseGet(List::of);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/invoices")
    @Operation(summary = "Get invoices of a store", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<InvoiceResource>> getInvoices(@RequestParam(required = false) String storeId) {
        if (storeId == null || storeId.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        var resources = billingQueryService.getInvoicesByStore(storeId).stream()
                .map(InvoiceResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(resources);
    }

    @PostMapping("/subscriptions")
    @Operation(summary = "Change the plan of a store", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SubscriptionResource> changePlan(@Valid @RequestBody ChangePlanResource resource) {
        var command = new ChangePlanCommand(resource.storeId(), resource.planId());
        return billingCommandService.handle(command)
                .map(subscription -> ResponseEntity.ok(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
