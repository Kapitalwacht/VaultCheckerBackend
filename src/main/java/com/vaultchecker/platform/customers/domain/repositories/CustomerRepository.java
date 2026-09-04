package com.vaultchecker.platform.customers.domain.repositories;

import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Customer repository port.
 */
public interface CustomerRepository {
    Optional<Customer> findById(Long id);

    List<Customer> findAll();

    List<Customer> findAllByStoreId(String storeId);

    Customer save(Customer customer);

    void deleteById(Long id);

    boolean existsById(Long id);
}
