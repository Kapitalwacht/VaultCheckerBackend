package com.vaultchecker.platform.credit.application.commandservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.commands.CreateCreditAccountCommand;
import com.vaultchecker.platform.credit.domain.model.commands.UpdateCreditAccountCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;

/**
 * Application service contract for credit account commands.
 */
public interface CreditAccountCommandService {
    Result<CreditAccount, ApplicationError> handle(CreateCreditAccountCommand command);

    Result<CreditAccount, ApplicationError> handle(UpdateCreditAccountCommand command);

    Result<Boolean, ApplicationError> handleDelete(Long id);
}
