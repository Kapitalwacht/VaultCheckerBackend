package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.InstallmentPaymentCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterInstallmentPaymentCommand;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class InstallmentPaymentCommandServiceImpl implements InstallmentPaymentCommandService {

    private final InstallmentPaymentRepository repository;

    public InstallmentPaymentCommandServiceImpl(InstallmentPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Result<InstallmentPayment, ApplicationError> handle(RegisterInstallmentPaymentCommand command) {
        if (command.purchaseId() == null || command.purchaseId().isBlank()) {
            return Result.failure(ApplicationError.validationError("purchaseId", "Purchase id is required"));
        }
        var payment = new InstallmentPayment();
        payment.setPaymentId(command.paymentId());
        payment.setStoreId(command.storeId());
        payment.setCustomerId(command.customerId());
        payment.setPurchaseId(command.purchaseId());
        payment.setPeriod(command.period());
        payment.setScheduledDate(command.scheduledDate());
        payment.setPaidDate(command.paidDate());
        if (command.installment() != null) payment.setInstallment(command.installment());
        if (command.lateFee() != null) payment.setLateFee(command.lateFee());
        if (command.interest() != null) payment.setInterest(command.interest());
        if (command.principal() != null) payment.setPrincipal(command.principal());
        if (command.total() != null) payment.setTotal(command.total());
        if (command.state() != null && !command.state().isBlank()) payment.setState(command.state());
        return Result.success(repository.save(payment));
    }
}
