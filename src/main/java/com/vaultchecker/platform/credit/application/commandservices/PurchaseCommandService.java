package com.vaultchecker.platform.credit.application.commandservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterPurchaseCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;

public interface PurchaseCommandService {
    Result<Purchase, ApplicationError> handle(RegisterPurchaseCommand command);
}
