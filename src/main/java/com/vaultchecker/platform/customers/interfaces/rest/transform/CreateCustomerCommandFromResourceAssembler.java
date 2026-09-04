package com.vaultchecker.platform.customers.interfaces.rest.transform;

import com.vaultchecker.platform.customers.domain.model.commands.CreateCustomerCommand;
import com.vaultchecker.platform.customers.interfaces.rest.resources.CreateCustomerResource;

/**
 * Assembler that translates {@link CreateCustomerResource} into {@link CreateCustomerCommand}.
 */
public class CreateCustomerCommandFromResourceAssembler {
    public static CreateCustomerCommand toCommandFromResource(CreateCustomerResource resource) {
        return new CreateCustomerCommand(resource.customerId(), resource.storeId(), resource.firstName(),
                resource.lastName(), resource.dni(), resource.phone(), resource.address(), resource.creditLimit());
    }
}
