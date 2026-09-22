package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.PurchaseCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterPurchaseCommand;
import com.vaultchecker.platform.credit.domain.model.valueobjects.AmortizationInstallment;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.credit.domain.services.CreditCycle;
import com.vaultchecker.platform.credit.domain.services.FrenchAmortizationCalculator;
import com.vaultchecker.platform.credit.domain.services.InterestCalculator;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PurchaseCommandServiceImpl implements PurchaseCommandService {

    private static final int DEFAULT_CUTOFF_DAY = 25;
    private static final int DEFAULT_PAYMENT_DAY = 5;

    private final PurchaseRepository purchaseRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final InstallmentPaymentRepository installmentPaymentRepository;
    private final CustomerRepository customerRepository;

    public PurchaseCommandServiceImpl(PurchaseRepository purchaseRepository,
                                      CreditAccountRepository creditAccountRepository,
                                      InstallmentPaymentRepository installmentPaymentRepository,
                                      CustomerRepository customerRepository) {
        this.purchaseRepository = purchaseRepository;
        this.creditAccountRepository = creditAccountRepository;
        this.installmentPaymentRepository = installmentPaymentRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Result<Purchase, ApplicationError> handle(RegisterPurchaseCommand command) {
        if (command.amount() == null || command.amount().signum() <= 0) {
            return Result.failure(ApplicationError.validationError("amount", "Purchase amount must be positive"));
        }

        var account = creditAccountRepository.findAllByCustomerId(command.customerId()).stream()
                .findFirst().orElse(null);

        if (account != null && account.wouldExceedLimit(command.amount())) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "credit-limit",
                    "Purchase of %s exceeds available credit (balance %s, limit %s)".formatted(
                            command.amount(), account.getBalance(), account.getCreditLimit())));
        }

        var purchase = new Purchase(command.purchaseId(), command.storeId(), command.customerId(),
                command.productId(), command.description(), command.quantity(), command.amount(),
                command.purchaseDate(), command.months(), command.state());
        var savedPurchase = purchaseRepository.save(purchase);

        if (account != null) {
            int months = command.months() == null ? 1 : command.months();
            if (months <= 1) {
                account.addCharge(command.amount());
            } else {
                generateInstallmentSchedule(command, account, months);
            }
            creditAccountRepository.save(account);
        }

        return Result.success(savedPurchase);
    }

    private void generateInstallmentSchedule(RegisterPurchaseCommand command, CreditAccount account, int months) {
        var customer = findCustomer(command.customerId());
        var effectiveAnnualRate = customer == null ? BigDecimal.ZERO : CreditCycle.effectiveAnnualRate(
                customer.getRateType(), customer.getRateValue(),
                customer.getRatePeriodDays(), customer.getRateCapitalizationDays());
        int cutoffDay = customer != null && customer.getCutoffDay() != null ? customer.getCutoffDay() : DEFAULT_CUTOFF_DAY;
        int paymentDay = customer != null && customer.getPaymentDay() != null ? customer.getPaymentDay() : DEFAULT_PAYMENT_DAY;

        var purchaseDate = command.purchaseDate() != null ? command.purchaseDate() : LocalDate.now();
        var graceEnd = CreditCycle.graceEndDate(purchaseDate, cutoffDay, paymentDay);
        int graceDays = CreditCycle.graceDays(purchaseDate, cutoffDay, paymentDay);

        for (AmortizationInstallment row : FrenchAmortizationCalculator.schedule(
                command.amount(), effectiveAnnualRate, months, graceDays)) {
            var installment = new InstallmentPayment();
            installment.setPaymentId(command.purchaseId() + "-C" + row.number());
            installment.setStoreId(command.storeId());
            installment.setCustomerId(command.customerId());
            installment.setPurchaseId(command.purchaseId());
            installment.setPeriod(row.number());

            installment.setScheduledDate(graceEnd.plusMonths(row.number()));
            installment.setPaidDate(null);
            installment.setInstallment(row.payment());
            installment.setLateFee(BigDecimal.ZERO);
            installment.setInterest(row.interest());
            installment.setPrincipal(row.principal());
            installment.setTotal(row.payment());
            installment.setState("pending");
            installmentPaymentRepository.save(installment);
        }

        account.addCharge(InterestCalculator.capitalizeForGrace(command.amount(), effectiveAnnualRate, graceDays));
        account.setDueDate(graceEnd.plusMonths(1));
    }

    private Customer findCustomer(String customerId) {
        if (customerId == null) return null;
        return customerRepository.findAll().stream()
                .filter(c -> customerId.equals(c.getCustomerId()))
                .findFirst().orElse(null);
    }
}
