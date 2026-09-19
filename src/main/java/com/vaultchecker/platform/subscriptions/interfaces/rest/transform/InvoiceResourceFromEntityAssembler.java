package com.vaultchecker.platform.subscriptions.interfaces.rest.transform;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Invoice;
import com.vaultchecker.platform.subscriptions.interfaces.rest.resources.InvoiceResource;

public class InvoiceResourceFromEntityAssembler {
    public static InvoiceResource toResourceFromEntity(Invoice invoice) {
        return new InvoiceResource(invoice.getId(), invoice.getInvoiceId(), invoice.getStoreId(),
                invoice.getPlanId(), invoice.getAmount(), invoice.getCurrency(), invoice.getStatus(), invoice.getDate());
    }
}
