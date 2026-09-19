package com.vaultchecker.platform.customers.interfaces.rest.transform;

import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.interfaces.rest.resources.CustomerResource;

public class CustomerResourceFromEntityAssembler {
    public static CustomerResource toResourceFromEntity(Customer customer) {
        return new CustomerResource(customer.getId(), customer.getCustomerId(), customer.getStoreId(),
                customer.getFirstName(), customer.getLastName(), customer.getDni(), customer.getPhone(),
                customer.getAddress(), customer.getCreditLimit(), customer.getCurrency(), customer.getRateType(),
                customer.getRateValue(), customer.getRateCapitalizationDays(), customer.getRatePeriodDays(),
                customer.getMoratoriumRateType(), customer.getMoratoriumRateValue(), customer.getMaxMonths(),
                customer.getCutoffDay(), customer.getPaymentDay(), customer.getState());
    }
}
