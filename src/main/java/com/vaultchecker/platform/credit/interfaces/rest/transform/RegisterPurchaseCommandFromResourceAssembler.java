package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.commands.RegisterPurchaseCommand;
import com.vaultchecker.platform.credit.interfaces.rest.resources.RegisterPurchaseResource;

public class RegisterPurchaseCommandFromResourceAssembler {
    public static RegisterPurchaseCommand toCommandFromResource(RegisterPurchaseResource resource) {
        return new RegisterPurchaseCommand(resource.purchaseId(), resource.storeId(), resource.customerId(),
                resource.productId(), resource.description(), resource.quantity(), resource.amount(),
                resource.purchaseDate(), resource.months(), resource.state());
    }
}
