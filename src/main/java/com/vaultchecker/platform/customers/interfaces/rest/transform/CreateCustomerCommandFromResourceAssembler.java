package com.vaultchecker.platform.customers.interfaces.rest.transform;

import com.vaultchecker.platform.customers.domain.model.commands.CreateCustomerCommand;
import com.vaultchecker.platform.customers.interfaces.rest.resources.CreateCustomerResource;

public class CreateCustomerCommandFromResourceAssembler {
    public static CreateCustomerCommand toCommandFromResource(CreateCustomerResource resource) {
        return new CreateCustomerCommand(resource.customerId(), resource.storeId(), resource.firstName(),
                resource.lastName(), resource.dni(), resource.phone(), resource.address(), resource.creditLimit(),
                resource.currency(), resource.rateType(), resource.rateValue(), resource.rateCapitalizationDays(),
                resource.ratePeriodDays(), resource.moratoriumRateType(), resource.moratoriumRateValue(),
                resource.maxMonths(), resource.cutoffDay(), resource.paymentDay());
    }
}
