package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.application.commandservices.PurchaseCommandService;
import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterPurchaseCommand;
import com.vaultchecker.platform.credit.domain.model.valueobjects.AmortizationInstallment;
import com.vaultchecker.platform.credit.domain.model.valueobjects.RateType;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.credit.domain.services.FrenchAmortizationCalculator;
import com.vaultchecker.platform.credit.domain.services.InterestCalculator;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Purchase command service. Beyond persisting the purchase it wires the financial engine into the flow:
 * <ul>
 *     <li>rejects purchases that would exceed the customer's available credit (brief §5);</li>
 *     <li>updates the credit account balance (fin de mes: full amount; instalments: capitalised principal);</li>
 *     <li>for the instalment modality, generates the French-method schedule with grace-period
 *         capitalisation and persists each {@link InstallmentPayment}.</li>
 * </ul>
 */
@Service
public class PurchaseCommandServiceImpl implements PurchaseCommandService {

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

        // Locate the customer's credit account (if any) to enforce the credit limit.
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
                // Fin de mes: the full amount is added to the running balance.
                account.addCharge(command.amount());
            } else {
                generateInstallmentSchedule(command, account, months);
            }
            creditAccountRepository.save(account);
        }

        return Result.success(savedPurchase);
    }

    /**
     * Builds the French-method schedule for an instalment purchase, capitalising the grace period, and
     * persists the resulting instalments. The capitalised principal becomes the account's new debt.
     */
    private void generateInstallmentSchedule(RegisterPurchaseCommand command, CreditAccount account, int months) {
        var customer = findCustomer(command.customerId());
        var effectiveAnnualRate = effectiveAnnualRateOf(customer);
        var purchaseDate = command.purchaseDate() != null ? command.purchaseDate() : LocalDate.now();

        // End of the grace period (first payment-day on/after the purchase, respecting the cutoff cycle).
        var graceEnd = graceEndDate(purchaseDate, customer);
        int graceDays = (int) Math.max(0, ChronoUnit.DAYS.between(purchaseDate, graceEnd));

        for (AmortizationInstallment row : FrenchAmortizationCalculator.schedule(
                command.amount(), effectiveAnnualRate, months, graceDays)) {
            var installment = new InstallmentPayment();
            installment.setPaymentId(command.purchaseId() + "-C" + row.number());
            installment.setStoreId(command.storeId());
            installment.setCustomerId(command.customerId());
            installment.setPurchaseId(command.purchaseId());
            installment.setPeriod(row.number());
            // First instalment falls one month after the grace period ends.
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

    /** Resolves the customer's effective annual rate from their pactada nominal/effective rate. */
    private BigDecimal effectiveAnnualRateOf(Customer customer) {
        if (customer == null || customer.getRateValue() == null) {
            return BigDecimal.ZERO;
        }
        var type = "nominal".equalsIgnoreCase(customer.getRateType()) ? RateType.NOMINAL : RateType.EFFECTIVE;
        int periodDays = customer.getRatePeriodDays() != null ? customer.getRatePeriodDays() : 360;
        int capDays = customer.getRateCapitalizationDays() != null && customer.getRateCapitalizationDays() > 0
                ? customer.getRateCapitalizationDays() : 30;
        int compoundingsPerYear = Math.max(1, periodDays / capDays);
        return InterestCalculator.toEffectiveAnnual(customer.getRateValue(), type, compoundingsPerYear);
    }

    /** First payment-day on/after the purchase date, respecting the cutoff cycle (grace-period end). */
    private LocalDate graceEndDate(LocalDate purchaseDate, Customer customer) {
        int cutoffDay = customer != null && customer.getCutoffDay() != null ? customer.getCutoffDay() : 25;
        int paymentDay = customer != null && customer.getPaymentDay() != null ? customer.getPaymentDay() : 5;
        var base = purchaseDate.getDayOfMonth() <= cutoffDay ? purchaseDate : purchaseDate.plusMonths(1);
        var anchor = atDay(base, paymentDay);
        if (!anchor.isAfter(purchaseDate)) {
            anchor = atDay(base.plusMonths(1), paymentDay);
        }
        return anchor;
    }

    private LocalDate atDay(LocalDate month, int day) {
        return month.withDayOfMonth(Math.min(day, month.lengthOfMonth()));
    }
}
