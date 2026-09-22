package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.commands.RegisterPaymentCommand;
import com.vaultchecker.platform.credit.interfaces.rest.resources.RegisterPaymentResource;

public class RegisterPaymentCommandFromResourceAssembler {
    public static RegisterPaymentCommand toCommandFromResource(RegisterPaymentResource resource) {
        return new RegisterPaymentCommand(resource.creditAccountId(), resource.storeId(), resource.amount(),
                resource.date(), resource.lateInterestDue(), resource.compensatoryInterestDue());
    }
}
