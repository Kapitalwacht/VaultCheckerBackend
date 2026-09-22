package com.vaultchecker.platform.credit.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class InstallmentPayment extends AbstractDomainAggregateRoot<InstallmentPayment> {

    @Setter private Long id;
    @Setter private String paymentId;
    @Setter private String storeId;
    @Setter private String customerId;
    @Setter private String purchaseId;
    @Setter private Integer period;
    @Setter private LocalDate scheduledDate;
    @Setter private LocalDate paidDate;
    @Setter private BigDecimal installment;
    @Setter private BigDecimal lateFee;
    @Setter private BigDecimal interest;
    @Setter private BigDecimal principal;
    @Setter private BigDecimal total;
    @Setter private String state;

    public static final String PAID = "paid";

    public InstallmentPayment() {
        this.installment = BigDecimal.ZERO;
        this.lateFee = BigDecimal.ZERO;
        this.interest = BigDecimal.ZERO;
        this.principal = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.state = PAID;
    }
}
