package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.InstallmentPaymentCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterInstallmentPaymentCommand;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class InstallmentPaymentCommandServiceImpl implements InstallmentPaymentCommandService {

    private static final String PAID = "paid";

    private final InstallmentPaymentRepository repository;
    private final CreditAccountRepository creditAccountRepository;

    public InstallmentPaymentCommandServiceImpl(InstallmentPaymentRepository repository,
                                                CreditAccountRepository creditAccountRepository) {
        this.repository = repository;
        this.creditAccountRepository = creditAccountRepository;
    }

    @Override
    public Result<InstallmentPayment, ApplicationError> handle(RegisterInstallmentPaymentCommand command) {
        if (command.purchaseId() == null || command.purchaseId().isBlank()) {
            return Result.failure(ApplicationError.validationError("purchaseId", "Purchase id is required"));
        }
        var scheduled = repository.findAllByPurchaseId(command.purchaseId()).stream()
                .filter(row -> Objects.equals(row.getPeriod(), command.period()))
                .findFirst();
        if (scheduled.isPresent() && PAID.equalsIgnoreCase(scheduled.get().getState())) {
            return Result.failure(ApplicationError.conflict("InstallmentPayment", "Installment already paid"));
        }

        var payment = scheduled.orElseGet(InstallmentPayment::new);
        if (payment.getPaymentId() == null) payment.setPaymentId(command.paymentId());
        payment.setStoreId(command.storeId());
        payment.setCustomerId(command.customerId());
        payment.setPurchaseId(command.purchaseId());
        payment.setPeriod(command.period());
        if (command.scheduledDate() != null) payment.setScheduledDate(command.scheduledDate());
        payment.setPaidDate(command.paidDate());
        if (command.installment() != null) payment.setInstallment(command.installment());
        if (command.lateFee() != null) payment.setLateFee(command.lateFee());
        if (command.interest() != null) payment.setInterest(command.interest());
        if (command.principal() != null) payment.setPrincipal(command.principal());
        if (command.total() != null) payment.setTotal(command.total());
        payment.setState(command.state() != null && !command.state().isBlank() ? command.state() : PAID);
        var saved = repository.save(payment);

        if (PAID.equalsIgnoreCase(saved.getState())) {
            reduceCustomerBalance(saved.getCustomerId(), saved.getPrincipal());
        }
        return Result.success(saved);
    }

    private void reduceCustomerBalance(String customerId, BigDecimal principal) {
        if (customerId == null || principal == null || principal.signum() <= 0) return;
        creditAccountRepository.findAllByCustomerId(customerId).stream().findFirst().ifPresent(account -> {
            account.reduceBalance(principal);
            creditAccountRepository.save(account);
        });
    }
}
