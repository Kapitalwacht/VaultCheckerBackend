package com.vaultchecker.platform.customers.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Resource received to update an existing customer.
 */
public record UpdateCustomerResource(
        @Size(max = 60) String customerId,
        @Size(max = 60) String storeId,
        @NotBlank @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,
        @Size(max = 20) String dni,
        @Size(max = 30) String phone,
        @Size(max = 200) String address,
        BigDecimal creditLimit,
        @Size(max = 20) String state
) {
}
