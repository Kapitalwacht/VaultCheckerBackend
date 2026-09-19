package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.PurchaseCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterPurchaseCommand;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class PurchaseCommandServiceImpl implements PurchaseCommandService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseCommandServiceImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Result<Purchase, ApplicationError> handle(RegisterPurchaseCommand command) {
        if (command.amount() == null || command.amount().signum() <= 0) {
            return Result.failure(ApplicationError.validationError("amount", "Purchase amount must be positive"));
        }
        var purchase = new Purchase(command.purchaseId(), command.storeId(), command.customerId(),
                command.productId(), command.description(), command.quantity(), command.amount(),
                command.purchaseDate(), command.months(), command.state());
        return Result.success(purchaseRepository.save(purchase));
    }
}
