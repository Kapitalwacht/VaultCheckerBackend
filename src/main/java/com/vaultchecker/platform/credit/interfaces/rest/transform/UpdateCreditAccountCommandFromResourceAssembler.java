package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.commands.UpdateCreditAccountCommand;
import com.vaultchecker.platform.credit.interfaces.rest.resources.UpdateCreditAccountResource;

/**
 * Assembler that translates {@link UpdateCreditAccountResource} into {@link UpdateCreditAccountCommand}.
 */
public class UpdateCreditAccountCommandFromResourceAssembler {
    public static UpdateCreditAccountCommand toCommandFromResource(Long id, UpdateCreditAccountResource resource) {
        return new UpdateCreditAccountCommand(id, resource.creditId(), resource.storeId(), resource.customerId(),
                resource.balance(), resource.creditLimit(), resource.dueDate(), resource.state());
    }
}
