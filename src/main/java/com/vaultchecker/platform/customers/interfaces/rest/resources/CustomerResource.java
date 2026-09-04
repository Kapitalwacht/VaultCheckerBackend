package com.vaultchecker.platform.customers.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Resource representing a customer returned by the REST API. Field names match the frontend.
 */
public record CustomerResource(Long id, String customerId, String storeId, String firstName, String lastName,
                               String dni, String phone, String address, BigDecimal creditLimit, String state) {
}
