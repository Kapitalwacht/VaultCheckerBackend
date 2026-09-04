package com.vaultchecker.platform.customers.interfaces.rest.transform;

import com.vaultchecker.platform.customers.domain.model.commands.UpdateCustomerCommand;
import com.vaultchecker.platform.customers.interfaces.rest.resources.UpdateCustomerResource;

/**
 * Assembler that translates {@link UpdateCustomerResource} into {@link UpdateCustomerCommand}.
 */
public class UpdateCustomerCommandFromResourceAssembler {
    public static UpdateCustomerCommand toCommandFromResource(Long id, UpdateCustomerResource resource) {
        return new UpdateCustomerCommand(id, resource.customerId(), resource.storeId(), resource.firstName(),
                resource.lastName(), resource.dni(), resource.phone(), resource.address(),
                resource.creditLimit(), resource.state());
    }
}
