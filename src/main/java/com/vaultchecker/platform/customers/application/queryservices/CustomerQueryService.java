package com.vaultchecker.platform.customers.application.queryservices;

import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.model.queries.GetAllCustomersQuery;
import com.vaultchecker.platform.customers.domain.model.queries.GetCustomerByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for customer read queries.
 */
public interface CustomerQueryService {
    List<Customer> handle(GetAllCustomersQuery query);

    Optional<Customer> handle(GetCustomerByIdQuery query);
}
