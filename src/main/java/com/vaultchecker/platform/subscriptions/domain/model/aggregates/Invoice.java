package com.vaultchecker.platform.subscriptions.domain.model.aggregates;

import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Invoice extends AbstractDomainAggregateRoot<Invoice> {

    @Setter
    private Long id;
    @Setter
    private String invoiceId;
    @Setter
    private String storeId;
    @Setter
    private String planId;
    @Setter
    private double amount;
    @Setter
    private String currency;
    @Setter
    private String status;
    @Setter
    private String date;

    public Invoice() {
        this.currency = "PEN";
        this.status = "paid";
    }

    public Invoice(String invoiceId, String storeId, String planId, double amount, String currency, String date) {
        this.invoiceId = invoiceId;
        this.storeId = storeId;
        this.planId = planId;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.status = "paid";
    }
}
