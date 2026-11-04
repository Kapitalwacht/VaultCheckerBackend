package com.vaultchecker.platform.credit.application.internal.queryservices;

import com.vaultchecker.platform.credit.application.queryservices.CreditStatementQueryService;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.queries.GetCreditStatementQuery;
import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditStatement;
import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditTerms;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.credit.domain.services.CreditCycle;
import com.vaultchecker.platform.credit.domain.services.InterestCalculator;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class CreditStatementQueryServiceImpl implements CreditStatementQueryService {

    private final CreditAccountRepository creditAccountRepository;
    private final PurchaseRepository purchaseRepository;
    private final InstallmentPaymentRepository installmentPaymentRepository;
    private final CustomerRepository customerRepository;

    public CreditStatementQueryServiceImpl(CreditAccountRepository creditAccountRepository,
                                           PurchaseRepository purchaseRepository,
                                           InstallmentPaymentRepository installmentPaymentRepository,
                                           CustomerRepository customerRepository) {
        this.creditAccountRepository = creditAccountRepository;
        this.purchaseRepository = purchaseRepository;
        this.installmentPaymentRepository = installmentPaymentRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<CreditStatement> handle(GetCreditStatementQuery query) {
        var customerId = query.customerId();
        if (customerId == null) {
            return Optional.empty();
        }
        var customer = customerRepository.findByReference(customerId).orElse(null);
        if (customer == null) {
            return Optional.empty();
        }
        var account = creditAccountRepository.findAllByCustomerId(customerId).stream().findFirst().orElse(null);

        var cutoffDate = query.asOfDate() != null ? query.asOfDate() : LocalDate.now();
        int cutoffDay = CreditCycle.cutoffDayOf(customer.getCutoffDay());
        int paymentDay = CreditCycle.paymentDayOf(customer.getPaymentDay());
        var paymentDate = CreditCycle.graceEndDate(cutoffDate, cutoffDay, paymentDay);

        var customerTerms = CreditTerms.from(customer);
        Map<String, BigDecimal> moratoryRates = new HashMap<>();

        List<CreditStatement.Line> lines = new ArrayList<>();
        var purchases = purchaseRepository.findAllByCustomerId(customerId).stream()
                .filter(p -> p.getPurchaseDate() != null && !p.getPurchaseDate().isAfter(cutoffDate))
                .sorted(Comparator.comparing(Purchase::getPurchaseDate))
                .toList();
        for (Purchase purchase : purchases) {
            var installments = installmentPaymentRepository.findAllByPurchaseId(purchase.getPurchaseId());
            int months = purchase.getMonths() == null ? 1 : purchase.getMonths();
            var terms = purchase.termsOr(customerTerms);
            moratoryRates.put(purchase.getPurchaseId(), terms.moratoryRate());
            if (months <= 1) {
                singlePaymentLine(purchase, installments, terms, paymentDate)
                        .ifPresent(lines::add);
            } else {
                installments.stream()
                        .filter(row -> !InstallmentPayment.PAID.equalsIgnoreCase(row.getState()))
                        .filter(row -> row.getScheduledDate() != null && !row.getScheduledDate().isAfter(paymentDate))
                        .sorted(Comparator.comparing(InstallmentPayment::getPeriod))
                        .map(row -> new CreditStatement.Line(
                                purchase.getPurchaseId(), purchase.getDescription(), purchase.getPurchaseDate(),
                                months, row.getPeriod(), row.getScheduledDate(),
                                InterestCalculator.scaleMoney(row.getInterest()),
                                InterestCalculator.scaleMoney(row.getPrincipal()),
                                InterestCalculator.scaleMoney(row.getInstallment())))
                        .forEach(lines::add);
            }
        }

        List<CreditStatement.LateFee> lateFees = lines.stream()
                .filter(line -> line.dueDate().isBefore(paymentDate))
                .map(line -> {
                    int daysLate = (int) ChronoUnit.DAYS.between(line.dueDate(), paymentDate);
                    var moratoryRate = moratoryRates.get(line.purchaseId());
                    return new CreditStatement.LateFee(
                            line.purchaseId(), line.description(), line.period(), line.dueDate(), daysLate,
                            line.total(), InterestCalculator.effectiveRateForDays(moratoryRate, daysLate),
                            InterestCalculator.moratoryInterest(line.total(), moratoryRate, daysLate));
                })
                .toList();

        var totalInterest = sum(lines.stream().map(CreditStatement.Line::interest).toList());
        var totalPrincipal = sum(lines.stream().map(CreditStatement.Line::principal).toList());
        var totalLateFee = sum(lateFees.stream().map(CreditStatement.LateFee::amount).toList());
        var totalToPay = sum(lines.stream().map(CreditStatement.Line::total).toList()).add(totalLateFee);

        return Optional.of(new CreditStatement(
                customerId,
                account != null ? account.getStoreId() : customer.getStoreId(),
                customer.getCurrency(),
                cutoffDate,
                paymentDate,
                lines,
                lateFees,
                totalInterest,
                totalPrincipal,
                totalLateFee,
                InterestCalculator.scaleMoney(totalToPay)));
    }

    private Optional<CreditStatement.Line> singlePaymentLine(Purchase purchase, List<InstallmentPayment> installments,
                                                             CreditTerms terms, LocalDate paymentDate) {
        boolean paid = installments.stream()
                .anyMatch(row -> Objects.equals(row.getPeriod(), 1) && InstallmentPayment.PAID.equalsIgnoreCase(row.getState()));
        var dueDate = terms.firstPaymentDate(purchase.getPurchaseDate());
        if (paid || dueDate.isAfter(paymentDate)) {
            return Optional.empty();
        }
        int days = (int) ChronoUnit.DAYS.between(purchase.getPurchaseDate(), dueDate);
        var principal = InterestCalculator.scaleMoney(purchase.getAmount());
        var interest = InterestCalculator.compensatoryInterest(purchase.getAmount(), terms.compensatoryRate(), days);
        return Optional.of(new CreditStatement.Line(
                purchase.getPurchaseId(), purchase.getDescription(), purchase.getPurchaseDate(),
                1, 1, dueDate, interest, principal, principal.add(interest)));
    }

    private static BigDecimal sum(List<BigDecimal> values) {
        return InterestCalculator.scaleMoney(values.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
