package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.PaymentCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.aggregates.Payment;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterPaymentCommand;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.PaymentRepository;
import com.vaultchecker.platform.credit.domain.services.CreditCycle;
import com.vaultchecker.platform.credit.domain.services.InterestCalculator;
import com.vaultchecker.platform.credit.domain.services.PaymentAllocationCalculator;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import com.vaultchecker.platform.shared.domain.model.events.AuditableActionRegisteredEvent;
import com.vaultchecker.platform.shared.infrastructure.security.CurrentUserRole;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private static final String REGISTER_PAYMENT_ACTION = "REGISTER_PAYMENT";

    private final CreditAccountRepository creditAccountRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PendingDebtService pendingDebtService;

    public PaymentCommandServiceImpl(CreditAccountRepository creditAccountRepository, PaymentRepository paymentRepository,
                                     CustomerRepository customerRepository, ApplicationEventPublisher eventPublisher,
                                     PendingDebtService pendingDebtService) {
        this.creditAccountRepository = creditAccountRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
        this.pendingDebtService = pendingDebtService;
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
        if (!pendingDebtService.pendingDueDates(account.getCustomerId()).isEmpty()) {
            return Result.failure(ApplicationError.conflict(
                    "PendingPurchases", "Pending purchases are paid one by one from the payment plan"));
        }
        var paymentDate = command.date() != null ? command.date() : LocalDate.now();

        int daysLate = daysLate(account, paymentDate);
        var lateDue = daysLate > 0
                ? InterestCalculator.moratoryInterest(account.getBalance(), moratoryRate(account), daysLate)
                : BigDecimal.ZERO;
        var compDue = command.compensatoryInterestDue() != null ? command.compensatoryInterestDue() : BigDecimal.ZERO;
        var principalDue = account.getBalance();

        var totalDue = principalDue.add(lateDue).add(compDue);
        if (command.amount().compareTo(totalDue) < 0) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "partial-payment",
                    "Partial payments are not allowed; the full amount due (%s) must be paid".formatted(totalDue)));
        }

        var allocation = PaymentAllocationCalculator.allocate(command.amount(), lateDue, compDue, principalDue);

        account.reduceBalance(allocation.toPrincipal());
        creditAccountRepository.save(account);

        var payment = new Payment(command.creditAccountId(), command.storeId(), command.amount(), paymentDate);
        payment.setAppliedToLateInterest(allocation.toLateInterest());
        payment.setAppliedToCompensatoryInterest(allocation.toCompensatoryInterest());
        payment.setAppliedToPrincipal(allocation.toPrincipal());
        var saved = paymentRepository.save(payment);

        eventPublisher.publishEvent(new AuditableActionRegisteredEvent(
                CurrentUserRole.clientRole(),
                REGISTER_PAYMENT_ACTION,
                "Payment of %s on credit account %d (late interest %s for %d days)".formatted(
                        command.amount(), command.creditAccountId(), allocation.toLateInterest(), daysLate)));
        return Result.success(saved);
    }

    private static int daysLate(CreditAccount account, LocalDate paymentDate) {
        if (account.getBalance().signum() <= 0 || account.getDueDate() == null || !account.getDueDate().isBefore(paymentDate)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(account.getDueDate(), paymentDate);
    }

    private BigDecimal moratoryRate(CreditAccount account) {
        return customerRepository.findByReference(account.getCustomerId())
                .map(customer -> CreditCycle.effectiveAnnualRate(
                        customer.getMoratoriumRateType(), customer.getMoratoriumRateValue(),
                        customer.getRatePeriodDays(), customer.getRateCapitalizationDays()))
                .orElse(BigDecimal.ZERO);
    }
}
