package com.vaultchecker.platform.credit.application.internal.commandservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.aggregates.Purchase;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.credit.domain.repositories.PurchaseRepository;
import com.vaultchecker.platform.credit.domain.services.CreditCycle;
import com.vaultchecker.platform.customers.domain.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class PendingDebtService {

    private final PurchaseRepository purchaseRepository;
    private final InstallmentPaymentRepository installmentPaymentRepository;
    private final CustomerRepository customerRepository;

    public PendingDebtService(PurchaseRepository purchaseRepository,
                              InstallmentPaymentRepository installmentPaymentRepository,
                              CustomerRepository customerRepository) {
        this.purchaseRepository = purchaseRepository;
        this.installmentPaymentRepository = installmentPaymentRepository;
        this.customerRepository = customerRepository;
    }

    public List<LocalDate> pendingDueDates(String customerId) {
        return purchaseRepository.findAllByCustomerId(customerId).stream()
                .flatMap(purchase -> pendingDueDates(customerId, purchase))
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    public void refreshDueDate(CreditAccount account) {
        pendingDueDates(account.getCustomerId()).stream().findFirst().ifPresent(account::setDueDate);
    }

    private Stream<LocalDate> pendingDueDates(String customerId, Purchase purchase) {
        var rows = installmentPaymentRepository.findAllByPurchaseId(purchase.getPurchaseId());
        if (purchase.getMonths() != null && purchase.getMonths() > 1) {
            return rows.stream()
                    .filter(row -> !InstallmentPayment.PAID.equalsIgnoreCase(row.getState()))
                    .map(InstallmentPayment::getScheduledDate)
                    .filter(Objects::nonNull);
        }
        boolean paid = rows.stream().anyMatch(row -> InstallmentPayment.PAID.equalsIgnoreCase(row.getState()));
        if (paid) {
            return Stream.empty();
        }
        return customerRepository.findByReference(customerId)
                .map(customer -> Stream.of(CreditCycle.graceEndDate(purchase.getPurchaseDate(),
                        CreditCycle.cutoffDayOf(customer.getCutoffDay()), CreditCycle.paymentDayOf(customer.getPaymentDay()))))
                .orElseGet(Stream::empty);
    }
}
