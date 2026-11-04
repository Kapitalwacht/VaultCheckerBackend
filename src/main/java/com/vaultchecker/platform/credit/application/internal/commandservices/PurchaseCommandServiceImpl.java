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
import com.vaultchecker.platform.catalog.domain.repositories.ProductRepository;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import com.vaultchecker.platform.shared.infrastructure.security.CurrentUserScope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PurchaseCommandServiceImpl implements PurchaseCommandService {


    private final PurchaseRepository purchaseRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final InstallmentPaymentRepository installmentPaymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PendingDebtService pendingDebtService;

    public PurchaseCommandServiceImpl(PurchaseRepository purchaseRepository,
                                      CreditAccountRepository creditAccountRepository,
                                      InstallmentPaymentRepository installmentPaymentRepository,
                                      CustomerRepository customerRepository,
                                      ProductRepository productRepository,
                                      PendingDebtService pendingDebtService) {
        this.purchaseRepository = purchaseRepository;
        this.creditAccountRepository = creditAccountRepository;
        this.installmentPaymentRepository = installmentPaymentRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.pendingDebtService = pendingDebtService;
    }

    @Override
    public Result<Purchase, ApplicationError> handle(RegisterPurchaseCommand command) {
        if (command.amount() == null || command.amount().signum() <= 0) {
            return Result.failure(ApplicationError.validationError("amount", "Purchase amount must be positive"));
        }
        var customer = customerRepository.findByReference(command.customerId()).orElse(null);
        if (customer == null) {
            return Result.failure(ApplicationError.notFound("Customer", String.valueOf(command.customerId())));
        }
        CurrentUserScope.requireStore(customer.getStoreId());
        int months = command.months() == null ? 1 : command.months();
        if (customer.getMaxMonths() != null && months > customer.getMaxMonths()) {
            return Result.failure(ApplicationError.validationError(
                    "months", "The customer only allows up to %d month(s)".formatted(customer.getMaxMonths())));
        }
        if (!productAllowsMonths(command.productId(), months)) {
            return Result.failure(ApplicationError.conflict(
                    "PaymentMode", "The product's payment mode does not allow %d month(s)".formatted(months)));
        }

        var account = creditAccountRepository.findAllByCustomerId(command.customerId()).stream()
                .findFirst()
                .orElseGet(() -> openAccount(command, customer));

        if (account.wouldExceedLimit(command.amount())) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "credit-limit",
                    "Purchase of %s exceeds available credit (balance %s, limit %s)".formatted(
                            command.amount(), account.getBalance(), account.getCreditLimit())));
        }

        var purchase = new Purchase(command.purchaseId(), command.storeId(), command.customerId(),
                command.productId(), command.description(), command.quantity(), command.amount(),
                command.purchaseDate(), command.months(), command.state());
        var savedPurchase = purchaseRepository.save(purchase);

        if (months <= 1) {
            account.addCharge(command.amount());
        } else {
            generateInstallmentSchedule(command, customer, account, months);
        }
        pendingDebtService.refreshDueDate(account);
        creditAccountRepository.save(account);

        return Result.success(savedPurchase);
    }

    private CreditAccount openAccount(RegisterPurchaseCommand command, Customer customer) {
        return new CreditAccount("CRD-%s".formatted(command.purchaseId()), command.storeId(), command.customerId(),
                BigDecimal.ZERO, customer.getCreditLimit(), null);
    }

    private void generateInstallmentSchedule(RegisterPurchaseCommand command, Customer customer,
                                             CreditAccount account, int months) {
        var effectiveAnnualRate = CreditCycle.effectiveAnnualRate(
                customer.getRateType(), customer.getRateValue(),
                customer.getRatePeriodDays(), customer.getRateCapitalizationDays());
        int cutoffDay = CreditCycle.cutoffDayOf(customer.getCutoffDay());
        int paymentDay = CreditCycle.paymentDayOf(customer.getPaymentDay());

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

            installment.setScheduledDate(CreditCycle.installmentDate(graceEnd, row.number(), paymentDay));
            installment.setPaidDate(null);
            installment.setInstallment(row.payment());
            installment.setLateFee(BigDecimal.ZERO);
            installment.setInterest(row.interest());
            installment.setPrincipal(row.principal());
            installment.setTotal(row.payment());
            installment.setState(InstallmentPayment.PENDING);
            installmentPaymentRepository.save(installment);
        }

        account.addCharge(InterestCalculator.capitalizeForGrace(command.amount(), effectiveAnnualRate, graceDays));
    }

    private boolean productAllowsMonths(String productId, int months) {
        return productRepository.findByReference(productId)
                .map(product -> product.allowsMonths(months))
                .orElse(true);
    }
}
