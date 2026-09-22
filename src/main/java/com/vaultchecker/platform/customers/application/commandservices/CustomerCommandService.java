package com.vaultchecker.platform.customers.application.commandservices;

import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.model.commands.CreateCustomerCommand;
import com.vaultchecker.platform.customers.domain.model.commands.DeleteCustomerCommand;
import com.vaultchecker.platform.customers.domain.model.commands.UpdateCustomerCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;

public interface CustomerCommandService {
    Result<Customer, ApplicationError> handle(CreateCustomerCommand command);

    Result<Customer, ApplicationError> handle(UpdateCustomerCommand command);

    Result<Boolean, ApplicationError> handle(DeleteCustomerCommand command);
}
