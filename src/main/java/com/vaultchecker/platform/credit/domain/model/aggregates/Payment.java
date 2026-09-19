package com.vaultchecker.platform.credit.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment aggregate root. Records a payment made against a credit account, keeping the allocation order
 * breakdown: how much went to moratory interest, compensatory interest and principal.
 */
@Getter
public class Payment extends AbstractDomainAggregateRoot<Payment> {

    @Setter
    private Long id;
    @Setter
    private Long creditAccountId;
    @Setter
    private String storeId;
    @Setter
    private BigDecimal amount;
    @Setter
    private LocalDate date;
    @Setter
    private BigDecimal appliedToLateInterest;
    @Setter
    private BigDecimal appliedToCompensatoryInterest;
    @Setter
    private BigDecimal appliedToPrincipal;

    public Payment() {
        this.amount = BigDecimal.ZERO;
        this.appliedToLateInterest = BigDecimal.ZERO;
        this.appliedToCompensatoryInterest = BigDecimal.ZERO;
        this.appliedToPrincipal = BigDecimal.ZERO;
    }

    public Payment(Long creditAccountId, String storeId, BigDecimal amount, LocalDate date) {
        this();
        this.creditAccountId = creditAccountId;
        this.storeId = storeId;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.date = date;
    }
}
