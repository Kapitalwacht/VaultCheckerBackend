package com.vaultchecker.platform.customers.application.internal.commandservices;

import com.vaultchecker.platform.customers.application.commandservices.CustomerCommandService;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.model.commands.CreateCustomerCommand;
import com.vaultchecker.platform.customers.domain.model.commands.DeleteCustomerCommand;
import com.vaultchecker.platform.customers.domain.model.commands.UpdateCustomerCommand;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Customer command service implementation.
 */
@Service
public class CustomerCommandServiceImpl implements CustomerCommandService {

    private final CustomerRepository customerRepository;

    public CustomerCommandServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Result<Customer, ApplicationError> handle(CreateCustomerCommand command) {
        if (command.firstName() == null || command.firstName().isBlank()) {
            return Result.failure(ApplicationError.validationError("firstName", "Customer first name is required"));
        }
        var customer = new Customer(command.customerId(), command.storeId(), command.firstName(), command.lastName(),
                command.dni(), command.phone(), command.address(), command.creditLimit());
        return Result.success(customerRepository.save(customer));
    }

    @Override
    public Result<Customer, ApplicationError> handle(UpdateCustomerCommand command) {
        var existing = customerRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Customer", String.valueOf(command.id())));
        }
        var customer = existing.get();
        customer.setCustomerId(command.customerId());
        customer.setStoreId(command.storeId());
        customer.setFirstName(command.firstName());
        customer.setLastName(command.lastName());
        customer.setDni(command.dni());
        customer.setPhone(command.phone());
        customer.setAddress(command.address());
        customer.setCreditLimit(command.creditLimit());
        if (command.state() != null && !command.state().isBlank()) {
            customer.setState(command.state());
        }
        return Result.success(customerRepository.save(customer));
    }

    @Override
    public Result<Boolean, ApplicationError> handle(DeleteCustomerCommand command) {
        if (!customerRepository.existsById(command.id())) {
            return Result.failure(ApplicationError.notFound("Customer", String.valueOf(command.id())));
        }
        customerRepository.deleteById(command.id());
        return Result.success(true);
    }
}
