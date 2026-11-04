package com.vaultchecker.platform.iam.infrastructure.customers;

import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.iam.application.internal.outboundservices.customers.CustomerDirectoryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerDirectoryServiceImpl implements CustomerDirectoryService {

    private final CustomerRepository customerRepository;

    public CustomerDirectoryServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<CustomerLink> findByDni(String dni) {
        if (dni == null || dni.isBlank()) {
            return Optional.empty();
        }
        return customerRepository.findAll().stream()
                .filter(customer -> dni.trim().equals(customer.getDni()))
                .findFirst()
                .map(customer -> new CustomerLink(String.valueOf(customer.getId()), customer.getStoreId()));
    }
}
