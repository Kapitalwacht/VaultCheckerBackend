package com.vaultchecker.platform.customers.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.infrastructure.persistence.jpa.entities.CustomerPersistenceEntity;

/**
 * Static assembler between customer domain and persistence representations.
 */
public final class CustomerPersistenceAssembler {

    private CustomerPersistenceAssembler() {
    }

    public static Customer toDomainFromPersistence(CustomerPersistenceEntity entity) {
        if (entity == null) return null;
        var customer = new Customer();
        customer.setId(entity.getId());
        customer.setCustomerId(entity.getCustomerId());
        customer.setStoreId(entity.getStoreId());
        customer.setFirstName(entity.getFirstName());
        customer.setLastName(entity.getLastName());
        customer.setDni(entity.getDni());
        customer.setPhone(entity.getPhone());
        customer.setAddress(entity.getAddress());
        customer.setCreditLimit(entity.getCreditLimit());
        customer.setState(entity.getState());
        return customer;
    }

    public static CustomerPersistenceEntity toPersistenceFromDomain(Customer customer) {
        if (customer == null) return null;
        var entity = new CustomerPersistenceEntity();
        if (customer.getId() != null) {
            entity.setId(customer.getId());
        }
        entity.setCustomerId(customer.getCustomerId());
        entity.setStoreId(customer.getStoreId());
        entity.setFirstName(customer.getFirstName());
        entity.setLastName(customer.getLastName());
        entity.setDni(customer.getDni());
        entity.setPhone(customer.getPhone());
        entity.setAddress(customer.getAddress());
        entity.setCreditLimit(customer.getCreditLimit());
        entity.setState(customer.getState());
        return entity;
    }
}
