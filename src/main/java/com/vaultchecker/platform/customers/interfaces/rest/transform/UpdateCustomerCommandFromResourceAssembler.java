package com.vaultchecker.platform.customers.interfaces.rest.transform;

import com.vaultchecker.platform.customers.domain.model.commands.UpdateCustomerCommand;
import com.vaultchecker.platform.customers.interfaces.rest.resources.UpdateCustomerResource;

public class UpdateCustomerCommandFromResourceAssembler {
    public static UpdateCustomerCommand toCommandFromResource(Long id, UpdateCustomerResource resource) {
        return new UpdateCustomerCommand(id, resource.customerId(), resource.storeId(), resource.firstName(),
                resource.lastName(), resource.dni(), resource.phone(), resource.address(), resource.creditLimit(),
                resource.currency(), resource.rateType(), resource.rateValue(), resource.rateCapitalizationDays(),
                resource.ratePeriodDays(), resource.moratoriumRateType(), resource.moratoriumRateValue(),
                resource.maxMonths(), resource.cutoffDay(), resource.paymentDay(), resource.state());
    }
}
