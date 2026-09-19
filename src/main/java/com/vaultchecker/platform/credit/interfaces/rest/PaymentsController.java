package com.vaultchecker.platform.credit.interfaces.rest;

import com.vaultchecker.platform.credit.application.commandservices.PaymentCommandService;
import com.vaultchecker.platform.credit.interfaces.rest.resources.RegisterPaymentResource;
import com.vaultchecker.platform.credit.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import com.vaultchecker.platform.credit.interfaces.rest.transform.RegisterPaymentCommandFromResourceAssembler;
import com.vaultchecker.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing payment registration. Applies the payment allocation order.
 */
@RestController
@RequestMapping(value = "/api/v1/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Payments", description = "Credit payment endpoints")
public class PaymentsController {
    private final PaymentCommandService paymentCommandService;

    public PaymentsController(PaymentCommandService paymentCommandService) {
        this.paymentCommandService = paymentCommandService;
    }

    @PostMapping
    @Operation(summary = "Register a payment", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> registerPayment(@Valid @RequestBody RegisterPaymentResource resource) {
        var command = RegisterPaymentCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = paymentCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, PaymentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
