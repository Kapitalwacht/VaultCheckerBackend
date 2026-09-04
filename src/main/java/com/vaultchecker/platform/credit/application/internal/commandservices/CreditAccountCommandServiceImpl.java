package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.CreditAccountCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.commands.CreateCreditAccountCommand;
import com.vaultchecker.platform.credit.domain.model.commands.UpdateCreditAccountCommand;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Credit account command service implementation.
 */
@Service
public class CreditAccountCommandServiceImpl implements CreditAccountCommandService {

    private final CreditAccountRepository creditAccountRepository;

    public CreditAccountCommandServiceImpl(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    @Override
    public Result<CreditAccount, ApplicationError> handle(CreateCreditAccountCommand command) {
        var account = new CreditAccount(command.creditId(), command.storeId(), command.customerId(),
                command.balance(), command.creditLimit(), command.dueDate());
        return Result.success(creditAccountRepository.save(account));
    }

    @Override
    public Result<CreditAccount, ApplicationError> handle(UpdateCreditAccountCommand command) {
        var existing = creditAccountRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("CreditAccount", String.valueOf(command.id())));
        }
        var account = existing.get();
        account.setCreditId(command.creditId());
        account.setStoreId(command.storeId());
        account.setCustomerId(command.customerId());
        account.setBalance(command.balance());
        account.setCreditLimit(command.creditLimit());
        account.setDueDate(command.dueDate());
        if (command.state() != null && !command.state().isBlank()) {
            account.setState(command.state());
        }
        return Result.success(creditAccountRepository.save(account));
    }

    @Override
    public Result<Boolean, ApplicationError> handleDelete(Long id) {
        if (!creditAccountRepository.existsById(id)) {
            return Result.failure(ApplicationError.notFound("CreditAccount", String.valueOf(id)));
        }
        creditAccountRepository.deleteById(id);
        return Result.success(true);
    }
}
