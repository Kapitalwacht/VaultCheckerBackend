package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.aggregates.Payment;
import com.vaultchecker.platform.credit.interfaces.rest.resources.PaymentResource;

/**
 * Assembler that converts a {@link Payment} aggregate into a {@link PaymentResource}.
 */
public class PaymentResourceFromEntityAssembler {
    public static PaymentResource toResourceFromEntity(Payment payment) {
        return new PaymentResource(payment.getId(), payment.getCreditAccountId(), payment.getStoreId(),
                payment.getAmount(), payment.getDate(), payment.getAppliedToLateInterest(),
                payment.getAppliedToCompensatoryInterest(), payment.getAppliedToPrincipal());
    }
}
