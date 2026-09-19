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
        applyTerms(customer, command.currency(), command.rateType(), command.rateValue(),
                command.rateCapitalizationDays(), command.ratePeriodDays(), command.moratoriumRateType(),
                command.moratoriumRateValue(), command.maxMonths(), command.cutoffDay(), command.paymentDay());
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
        applyTerms(customer, command.currency(), command.rateType(), command.rateValue(),
                command.rateCapitalizationDays(), command.ratePeriodDays(), command.moratoriumRateType(),
                command.moratoriumRateValue(), command.maxMonths(), command.cutoffDay(), command.paymentDay());
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

    private void applyTerms(Customer customer, String currency, String rateType, java.math.BigDecimal rateValue,
                            Integer rateCapitalizationDays, Integer ratePeriodDays, String moratoriumRateType,
                            java.math.BigDecimal moratoriumRateValue, Integer maxMonths, Integer cutoffDay,
                            Integer paymentDay) {
        if (currency != null) customer.setCurrency(currency);
        if (rateType != null) customer.setRateType(rateType);
        if (rateValue != null) customer.setRateValue(rateValue);
        if (rateCapitalizationDays != null) customer.setRateCapitalizationDays(rateCapitalizationDays);
        if (ratePeriodDays != null) customer.setRatePeriodDays(ratePeriodDays);
        if (moratoriumRateType != null) customer.setMoratoriumRateType(moratoriumRateType);
        if (moratoriumRateValue != null) customer.setMoratoriumRateValue(moratoriumRateValue);
        if (maxMonths != null) customer.setMaxMonths(maxMonths);
        if (cutoffDay != null) customer.setCutoffDay(cutoffDay);
        if (paymentDay != null) customer.setPaymentDay(paymentDay);
    }
}
