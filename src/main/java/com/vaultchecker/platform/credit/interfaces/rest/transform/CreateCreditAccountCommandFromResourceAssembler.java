package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.commands.CreateCreditAccountCommand;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreateCreditAccountResource;

public class CreateCreditAccountCommandFromResourceAssembler {
    public static CreateCreditAccountCommand toCommandFromResource(CreateCreditAccountResource resource) {
        return new CreateCreditAccountCommand(resource.creditId(), resource.storeId(), resource.customerId(),
                resource.balance(), resource.creditLimit(), resource.dueDate());
    }
}
