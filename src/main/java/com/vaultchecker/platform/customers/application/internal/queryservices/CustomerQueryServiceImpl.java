package com.vaultchecker.platform.customers.application.internal.queryservices;

import com.vaultchecker.platform.customers.application.queryservices.CustomerQueryService;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.model.queries.GetAllCustomersQuery;
import com.vaultchecker.platform.customers.domain.model.queries.GetCustomerByIdQuery;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Customer query service implementation.
 */
@Service
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerRepository customerRepository;

    public CustomerQueryServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> handle(GetAllCustomersQuery query) {
        if (query.storeId() != null && !query.storeId().isBlank()) {
            return customerRepository.findAllByStoreId(query.storeId());
        }
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> handle(GetCustomerByIdQuery query) {
        return customerRepository.findById(query.id());
    }
}
