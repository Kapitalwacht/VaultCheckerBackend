package com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Invoice;
import com.vaultchecker.platform.subscriptions.infrastructure.persistence.jpa.entities.InvoicePersistenceEntity;

public final class InvoicePersistenceAssembler {

    private InvoicePersistenceAssembler() {
    }

    public static Invoice toDomainFromPersistence(InvoicePersistenceEntity entity) {
        if (entity == null) return null;
        var invoice = new Invoice();
        invoice.setId(entity.getId());
        invoice.setInvoiceId(entity.getInvoiceId());
        invoice.setStoreId(entity.getStoreId());
        invoice.setPlanId(entity.getPlanId());
        invoice.setAmount(entity.getAmount());
        invoice.setCurrency(entity.getCurrency());
        invoice.setStatus(entity.getStatus());
        invoice.setDate(entity.getDate());
        return invoice;
    }

    public static InvoicePersistenceEntity toPersistenceFromDomain(Invoice invoice) {
        if (invoice == null) return null;
        var entity = new InvoicePersistenceEntity();
        if (invoice.getId() != null) {
            entity.setId(invoice.getId());
        }
        entity.setInvoiceId(invoice.getInvoiceId());
        entity.setStoreId(invoice.getStoreId());
        entity.setPlanId(invoice.getPlanId());
        entity.setAmount(invoice.getAmount());
        entity.setCurrency(invoice.getCurrency());
        entity.setStatus(invoice.getStatus());
        entity.setDate(invoice.getDate());
        return entity;
    }
}
