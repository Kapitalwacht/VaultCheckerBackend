package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.entities.InstallmentPaymentPersistenceEntity;

public final class InstallmentPaymentPersistenceAssembler {

    private InstallmentPaymentPersistenceAssembler() {
    }

    public static InstallmentPayment toDomainFromPersistence(InstallmentPaymentPersistenceEntity entity) {
        if (entity == null) return null;
        var payment = new InstallmentPayment();
        payment.setId(entity.getId());
        payment.setPaymentId(entity.getPaymentId());
        payment.setStoreId(entity.getStoreId());
        payment.setCustomerId(entity.getCustomerId());
        payment.setPurchaseId(entity.getPurchaseId());
        payment.setPeriod(entity.getPeriod());
        payment.setScheduledDate(entity.getScheduledDate());
        payment.setPaidDate(entity.getPaidDate());
        payment.setInstallment(entity.getInstallment());
        payment.setLateFee(entity.getLateFee());
        payment.setInterest(entity.getInterest());
        payment.setPrincipal(entity.getPrincipal());
        payment.setTotal(entity.getTotal());
        payment.setState(entity.getState());
        return payment;
    }

    public static InstallmentPaymentPersistenceEntity toPersistenceFromDomain(InstallmentPayment payment) {
        if (payment == null) return null;
        var entity = new InstallmentPaymentPersistenceEntity();
        if (payment.getId() != null) {
            entity.setId(payment.getId());
        }
        entity.setPaymentId(payment.getPaymentId());
        entity.setStoreId(payment.getStoreId());
        entity.setCustomerId(payment.getCustomerId());
        entity.setPurchaseId(payment.getPurchaseId());
        entity.setPeriod(payment.getPeriod());
        entity.setScheduledDate(payment.getScheduledDate());
        entity.setPaidDate(payment.getPaidDate());
        entity.setInstallment(payment.getInstallment());
        entity.setLateFee(payment.getLateFee());
        entity.setInterest(payment.getInterest());
        entity.setPrincipal(payment.getPrincipal());
        entity.setTotal(payment.getTotal());
        entity.setState(payment.getState());
        return entity;
    }
}
