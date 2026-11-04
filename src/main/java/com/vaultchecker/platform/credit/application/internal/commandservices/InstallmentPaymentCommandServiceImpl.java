package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.InstallmentPaymentCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterInstallmentPaymentCommand;
import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditTerms;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.credit.domain.services.InterestCalculator;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import com.vaultchecker.platform.shared.infrastructure.security.CurrentUserScope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class InstallmentPaymentCommandServiceImpl implements InstallmentPaymentCommandService {

    private final InstallmentPaymentRepository repository;
    private final CreditAccountRepository creditAccountRepository;
    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;
    private final PendingDebtService pendingDebtService;

    public InstallmentPaymentCommandServiceImpl(InstallmentPaymentRepository repository,
                                                CreditAccountRepository creditAccountRepository,
                                                PurchaseRepository purchaseRepository,
                                                CustomerRepository customerRepository,
                                                PendingDebtService pendingDebtService) {
        this.repository = repository;
        this.creditAccountRepository = creditAccountRepository;
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
        this.pendingDebtService = pendingDebtService;
    }

    @Override
    public Result<InstallmentPayment, ApplicationError> handle(RegisterInstallmentPaymentCommand command) {
        if (command.purchaseId() == null || command.purchaseId().isBlank()) {
            return Result.failure(ApplicationError.validationError("purchaseId", "Purchase id is required"));
        }
        var rows = repository.findAllByPurchaseId(command.purchaseId());
        var scheduled = rows.stream()
                .filter(row -> Objects.equals(row.getPeriod(), command.period()))
                .findFirst();
        if (scheduled.isPresent() && InstallmentPayment.PAID.equalsIgnoreCase(scheduled.get().getState())) {
            return Result.failure(ApplicationError.conflict("InstallmentPayment", "Installment already paid"));
        }
        boolean previousPending = rows.stream()
                .anyMatch(row -> row.getPeriod() < command.period() && !InstallmentPayment.PAID.equalsIgnoreCase(row.getState()));
        if (previousPending) {
            return Result.failure(ApplicationError.validationError("period", "The previous installment is still pending"));
        }

        var customer = customerRepository.findByReference(command.customerId()).orElse(null);
        CurrentUserScope.requireStore(customer != null ? customer.getStoreId() : command.storeId());
        var purchase = purchaseRepository.findAllByCustomerId(command.customerId()).stream()
                .filter(item -> command.purchaseId().equals(item.getPurchaseId()))
                .findFirst().orElse(null);
        var terms = agreedTerms(purchase, customer);
        var payment = scheduled.orElseGet(() -> singlePaymentRow(command, purchase, terms));
        var paidDate = command.paidDate() != null ? command.paidDate() : LocalDate.now();
        var lateFee = lateFee(payment, terms, paidDate);
        payment.setPaidDate(paidDate);
        payment.setLateFee(lateFee);
        payment.setTotal(InterestCalculator.scaleMoney(payment.getInstallment().add(lateFee)));
        payment.setState(command.state() != null && !command.state().isBlank() ? command.state() : InstallmentPayment.PAID);
        var saved = repository.save(payment);

        if (InstallmentPayment.PAID.equalsIgnoreCase(saved.getState())) {
            reduceCustomerBalance(saved.getCustomerId(), saved.getPrincipal());
        }
        return Result.success(saved);
    }

    private static CreditTerms agreedTerms(Purchase purchase, Customer customer) {
        var customerTerms = customer != null ? CreditTerms.from(customer) : null;
        return purchase != null ? purchase.termsOr(customerTerms) : customerTerms;
    }

    private InstallmentPayment singlePaymentRow(RegisterInstallmentPaymentCommand command, Purchase purchase, CreditTerms terms) {
        var payment = new InstallmentPayment();
        payment.setPaymentId(command.paymentId());
        payment.setStoreId(command.storeId());
        payment.setCustomerId(command.customerId());
        payment.setPurchaseId(command.purchaseId());
        payment.setPeriod(command.period());
        if (purchase != null && terms != null) {
            fillSinglePayment(payment, purchase, terms);
        }
        return payment;
    }

    private void fillSinglePayment(InstallmentPayment payment, Purchase purchase, CreditTerms terms) {
        var dueDate = terms.firstPaymentDate(purchase.getPurchaseDate());
        int days = (int) ChronoUnit.DAYS.between(purchase.getPurchaseDate(), dueDate);
        var principal = InterestCalculator.scaleMoney(purchase.getAmount());
        var interest = InterestCalculator.compensatoryInterest(purchase.getAmount(), terms.compensatoryRate(), days);
        payment.setScheduledDate(dueDate);
        payment.setPrincipal(principal);
        payment.setInterest(interest);
        payment.setInstallment(principal.add(interest));
    }

    private BigDecimal lateFee(InstallmentPayment payment, CreditTerms terms, LocalDate paidDate) {
        if (terms == null || payment.getScheduledDate() == null) {
            return InterestCalculator.scaleMoney(BigDecimal.ZERO);
        }
        int daysLate = (int) ChronoUnit.DAYS.between(payment.getScheduledDate(), paidDate);
        return InterestCalculator.moratoryInterest(payment.getInstallment(), terms.moratoryRate(), daysLate);
    }

    private void reduceCustomerBalance(String customerId, BigDecimal principal) {
        if (customerId == null || principal == null || principal.signum() <= 0) return;
        creditAccountRepository.findAllByCustomerId(customerId).stream().findFirst().ifPresent(account -> {
            account.reduceBalance(principal);
            pendingDebtService.refreshDueDate(account);
            creditAccountRepository.save(account);
        });
    }
}
