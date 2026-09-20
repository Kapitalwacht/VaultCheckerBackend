package com.vaultchecker.platform.credit.application.internal.queryservices;

import com.vaultchecker.platform.credit.application.queryservices.CreditStatementQueryService;
import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.model.queries.GetCreditStatementQuery;
import com.vaultchecker.platform.credit.domain.model.valueobjects.CreditStatement;
import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.credit.domain.services.CreditCycle;
import com.vaultchecker.platform.credit.domain.services.InterestCalculator;
import com.vaultchecker.platform.customers.domain.model.aggregates.Customer;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Builds a customer's cutoff-date statement (US-21): fin-de-mes purchases ordered by date with their
 * compensatory interest, plus moratory interest when the account is overdue, and the total to pay.
 */
@Service
public class CreditStatementQueryServiceImpl implements CreditStatementQueryService {

    private static final int DEFAULT_CUTOFF_DAY = 25;
    private static final int DEFAULT_PAYMENT_DAY = 5;

    private final CreditAccountRepository creditAccountRepository;
    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;

    public CreditStatementQueryServiceImpl(CreditAccountRepository creditAccountRepository,
                                           PurchaseRepository purchaseRepository,
                                           CustomerRepository customerRepository) {
        this.creditAccountRepository = creditAccountRepository;
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<CreditStatement> handle(GetCreditStatementQuery query) {
        var customerId = query.customerId();
        if (customerId == null) {
            return Optional.empty();
        }
        var customer = customerRepository.findAll().stream()
                .filter(c -> customerId.equals(c.getCustomerId()))
                .findFirst().orElse(null);
        var account = creditAccountRepository.findAllByCustomerId(customerId).stream()
                .findFirst().orElse(null);
        if (account == null) {
            return Optional.empty();
        }

        var asOf = query.asOfDate() != null ? query.asOfDate() : LocalDate.now();
        var effectiveRate = customer == null ? BigDecimal.ZERO : CreditCycle.effectiveAnnualRate(
                customer.getRateType(), customer.getRateValue(),
                customer.getRatePeriodDays(), customer.getRateCapitalizationDays());
        var moratoryRate = customer == null ? BigDecimal.ZERO : CreditCycle.effectiveAnnualRate(
                customer.getMoratoriumRateType(), customer.getMoratoriumRateValue(),
                customer.getRatePeriodDays(), customer.getRateCapitalizationDays());
        int cutoffDay = customer != null && customer.getCutoffDay() != null ? customer.getCutoffDay() : DEFAULT_CUTOFF_DAY;
        int paymentDay = customer != null && customer.getPaymentDay() != null ? customer.getPaymentDay() : DEFAULT_PAYMENT_DAY;

        var finDeMesPurchases = purchaseRepository.findAllByCustomerId(customerId).stream()
                .filter(p -> p.getPurchaseDate() != null)
                .filter(p -> p.getMonths() == null || p.getMonths() <= 1)
                .filter(p -> !"paid".equalsIgnoreCase(p.getState()))
                .sorted(Comparator.comparing(Purchase::getPurchaseDate))
                .toList();

        List<CreditStatement.Line> lines = new ArrayList<>();
        BigDecimal totalCompensatory = BigDecimal.ZERO;
        for (Purchase p : finDeMesPurchases) {
            var paymentDate = CreditCycle.graceEndDate(p.getPurchaseDate(), cutoffDay, paymentDay);
            int days = (int) Math.max(0, ChronoUnit.DAYS.between(p.getPurchaseDate(), paymentDate));
            var compensatory = InterestCalculator.compensatoryInterest(p.getAmount(), effectiveRate, days);
            totalCompensatory = totalCompensatory.add(compensatory);
            lines.add(new CreditStatement.Line(
                    p.getPurchaseId(), p.getDescription(), p.getPurchaseDate(), p.getAmount(), days, compensatory));
        }

        var principal = account.getBalance();
        var moratory = moratoryInterest(account, moratoryRate, asOf);
        var totalToPay = InterestCalculator.scaleMoney(principal.add(totalCompensatory).add(moratory));

        return Optional.of(new CreditStatement(
                customerId,
                account.getStoreId(),
                account.getDueDate(),
                account.getState(),
                lines,
                InterestCalculator.scaleMoney(principal),
                InterestCalculator.scaleMoney(totalCompensatory),
                InterestCalculator.scaleMoney(moratory),
                totalToPay));
    }

    private BigDecimal moratoryInterest(CreditAccount account, BigDecimal moratoryRate, LocalDate asOf) {
        if (!account.isOverdue() || account.getDueDate() == null || !account.getDueDate().isBefore(asOf)) {
            return BigDecimal.ZERO;
        }
        int daysLate = (int) Math.max(0, ChronoUnit.DAYS.between(account.getDueDate(), asOf));
        return InterestCalculator.moratoryInterest(account.getBalance(), moratoryRate, daysLate);
    }
}
