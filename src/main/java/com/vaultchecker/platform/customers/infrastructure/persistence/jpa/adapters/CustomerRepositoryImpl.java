package com.vaultchecker.platform.customers.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.customers.infrastructure.persistence.jpa.assemblers.CustomerPersistenceAssembler;
import com.vaultchecker.platform.customers.infrastructure.persistence.jpa.repositories.CustomerPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerPersistenceRepository customerPersistenceRepository;

    public CustomerRepositoryImpl(CustomerPersistenceRepository customerPersistenceRepository) {
        this.customerPersistenceRepository = customerPersistenceRepository;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerPersistenceRepository.findById(id).map(CustomerPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Customer> findAll() {
        return customerPersistenceRepository.findAll().stream().map(CustomerPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Customer> findAllByStoreId(String storeId) {
        return customerPersistenceRepository.findAllByStoreId(storeId).stream().map(CustomerPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Customer save(Customer customer) {
        var saved = customerPersistenceRepository.save(CustomerPersistenceAssembler.toPersistenceFromDomain(customer));
        return CustomerPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public void deleteById(Long id) {
        customerPersistenceRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return customerPersistenceRepository.existsById(id);
    }
}
