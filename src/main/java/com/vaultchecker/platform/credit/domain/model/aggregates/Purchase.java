package com.vaultchecker.platform.credit.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Purchase aggregate root. A credit purchase a customer makes at a store. Its list-price amount
 * and month count feed the French amortization / payment plan.
 */
@Getter
public class Purchase extends AbstractDomainAggregateRoot<Purchase> {

    @Setter private Long id;
    @Setter private String purchaseId;
    @Setter private String storeId;
    @Setter private String customerId;
    @Setter private String productId;
    @Setter private String description;
    @Setter private Integer quantity;
    @Setter private BigDecimal amount;
    @Setter private LocalDate purchaseDate;
    @Setter private Integer months;
    @Setter private String state;

    public static final String ACTIVE = "active";

    public Purchase() {
        this.quantity = 1;
        this.amount = BigDecimal.ZERO;
        this.months = 1;
        this.state = ACTIVE;
    }

    public Purchase(String purchaseId, String storeId, String customerId, String productId, String description,
                    Integer quantity, BigDecimal amount, LocalDate purchaseDate, Integer months, String state) {
        this();
        this.purchaseId = purchaseId;
        this.storeId = storeId;
        this.customerId = customerId;
        this.productId = productId;
        this.description = description;
        if (quantity != null) this.quantity = quantity;
        if (amount != null) this.amount = amount;
        if (purchaseDate != null) this.purchaseDate = purchaseDate;
        if (months != null) this.months = months;
        if (state != null && !state.isBlank()) this.state = state;
    }
}
