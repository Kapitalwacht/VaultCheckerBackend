package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.interfaces.rest.resources.InstallmentPaymentResource;

public class InstallmentPaymentResourceFromEntityAssembler {
    public static InstallmentPaymentResource toResourceFromEntity(InstallmentPayment payment) {
        return new InstallmentPaymentResource(payment.getId(), payment.getPaymentId(), payment.getStoreId(),
                payment.getCustomerId(), payment.getPurchaseId(), payment.getPeriod(), payment.getScheduledDate(),
                payment.getPaidDate(), payment.getInstallment(), payment.getLateFee(), payment.getInterest(),
                payment.getPrincipal(), payment.getTotal(), payment.getState());
    }
}
