package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.PaymentCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.Payment;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterPaymentCommand;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.PaymentRepository;
import com.vaultchecker.platform.credit.domain.services.PaymentAllocationCalculator;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Payment command service implementation. Applies the payment prelación (US-19): the amount is imputed
 * to moratory interest, then compensatory interest, then principal; the credit account balance is then
 * reduced by the principal portion.
 */
@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final CreditAccountRepository creditAccountRepository;
    private final PaymentRepository paymentRepository;

    public PaymentCommandServiceImpl(CreditAccountRepository creditAccountRepository, PaymentRepository paymentRepository) {
        this.creditAccountRepository = creditAccountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Result<Payment, ApplicationError> handle(RegisterPaymentCommand command) {
        if (command.amount() == null || command.amount().signum() <= 0) {
            return Result.failure(ApplicationError.validationError("amount", "Payment amount must be positive"));
        }
        var accountOpt = creditAccountRepository.findById(command.creditAccountId());
        if (accountOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("CreditAccount", String.valueOf(command.creditAccountId())));
        }
        var account = accountOpt.get();

        var lateDue = command.lateInterestDue() != null ? command.lateInterestDue() : BigDecimal.ZERO;
        var compDue = command.compensatoryInterestDue() != null ? command.compensatoryInterestDue() : BigDecimal.ZERO;
        var principalDue = account.getBalance();

        var allocation = PaymentAllocationCalculator.allocate(command.amount(), lateDue, compDue, principalDue);

        account.reduceBalance(allocation.toPrincipal());
        creditAccountRepository.save(account);

        var payment = new Payment(command.creditAccountId(), command.storeId(), command.amount(), command.date());
        payment.setAppliedToLateInterest(allocation.toLateInterest());
        payment.setAppliedToCompensatoryInterest(allocation.toCompensatoryInterest());
        payment.setAppliedToPrincipal(allocation.toPrincipal());
        return Result.success(paymentRepository.save(payment));
    }
}
