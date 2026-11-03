package com.vaultchecker.platform.customers.domain.repositories;

import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(Long id);

    List<Customer> findAll();

    default Optional<Customer> findByReference(String reference) {
        if (reference == null) return Optional.empty();
        return findAll().stream()
                .filter(customer -> reference.equals(customer.getCustomerId())
                        || reference.equals(String.valueOf(customer.getId())))
                .findFirst();
    }

    List<Customer> findAllByStoreId(String storeId);

    Customer save(Customer customer);

    void deleteById(Long id);

    boolean existsById(Long id);
}
