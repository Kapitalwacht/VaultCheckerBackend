package com.vaultchecker.platform.credit.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Credit account aggregate root. Represents a customer's running account (cuenta corriente) at a
 * store: the outstanding {@code balance}, the {@code creditLimit} and the {@code dueDate}. Scoped to a
 * store ({@code storeId}) for tenant isolation (US-03).
 */
@Getter
public class CreditAccount extends AbstractDomainAggregateRoot<CreditAccount> {

    @Setter
    private Long id;
    @Setter
    private String creditId;
    @Setter
    private String storeId;
    @Setter
    private String customerId;
    @Setter
    private BigDecimal balance;
    @Setter
    private BigDecimal creditLimit;
    @Setter
    private LocalDate dueDate;
    @Setter
    private String state;

    public static final String CURRENT = "current";
    public static final String OVERDUE = "overdue";

    public CreditAccount() {
        this.balance = BigDecimal.ZERO;
        this.creditLimit = BigDecimal.ZERO;
        this.state = CURRENT;
    }

    public CreditAccount(String creditId, String storeId, String customerId, BigDecimal balance,
                         BigDecimal creditLimit, LocalDate dueDate) {
        this.creditId = creditId;
        this.storeId = storeId;
        this.customerId = customerId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.creditLimit = creditLimit != null ? creditLimit : BigDecimal.ZERO;
        this.dueDate = dueDate;
        this.state = CURRENT;
    }

    /** Available credit = limit - balance (never negative). */
    public BigDecimal availableCredit() {
        var available = creditLimit.subtract(balance);
        return available.signum() < 0 ? BigDecimal.ZERO : available;
    }

    /** Whether a charge of {@code amount} would exceed the credit limit (US-14). */
    public boolean wouldExceedLimit(BigDecimal amount) {
        return balance.add(amount).compareTo(creditLimit) > 0;
    }

    /** Adds a charge to the outstanding balance. */
    public void addCharge(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    /** Reduces the outstanding balance (after a payment) without going below zero. */
    public void reduceBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount).max(BigDecimal.ZERO);
    }

    public boolean isOverdue() {
        return OVERDUE.equalsIgnoreCase(this.state);
    }
}
