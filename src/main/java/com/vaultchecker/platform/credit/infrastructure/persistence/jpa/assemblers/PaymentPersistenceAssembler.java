package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.credit.domain.model.aggregates.Payment;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;

/**
 * Static assembler between payment domain and persistence representations.
 */
public final class PaymentPersistenceAssembler {

    private PaymentPersistenceAssembler() {
    }

    public static Payment toDomainFromPersistence(PaymentPersistenceEntity entity) {
        if (entity == null) return null;
        var payment = new Payment();
        payment.setId(entity.getId());
        payment.setCreditAccountId(entity.getCreditAccountId());
        payment.setStoreId(entity.getStoreId());
        payment.setAmount(entity.getAmount());
        payment.setDate(entity.getDate());
        payment.setAppliedToLateInterest(entity.getAppliedToLateInterest());
        payment.setAppliedToCompensatoryInterest(entity.getAppliedToCompensatoryInterest());
        payment.setAppliedToPrincipal(entity.getAppliedToPrincipal());
        return payment;
    }

    public static PaymentPersistenceEntity toPersistenceFromDomain(Payment payment) {
        if (payment == null) return null;
        var entity = new PaymentPersistenceEntity();
        if (payment.getId() != null) {
            entity.setId(payment.getId());
        }
        entity.setCreditAccountId(payment.getCreditAccountId());
        entity.setStoreId(payment.getStoreId());
        entity.setAmount(payment.getAmount());
        entity.setDate(payment.getDate());
        entity.setAppliedToLateInterest(payment.getAppliedToLateInterest());
        entity.setAppliedToCompensatoryInterest(payment.getAppliedToCompensatoryInterest());
        entity.setAppliedToPrincipal(payment.getAppliedToPrincipal());
        return entity;
    }
}
