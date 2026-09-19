package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.commands.RegisterInstallmentPaymentCommand;
import com.vaultchecker.platform.credit.interfaces.rest.resources.RegisterInstallmentPaymentResource;

public class RegisterInstallmentPaymentCommandFromResourceAssembler {
    public static RegisterInstallmentPaymentCommand toCommandFromResource(RegisterInstallmentPaymentResource resource) {
        return new RegisterInstallmentPaymentCommand(resource.paymentId(), resource.storeId(), resource.customerId(),
                resource.purchaseId(), resource.period(), resource.scheduledDate(), resource.paidDate(),
                resource.installment(), resource.lateFee(), resource.interest(), resource.principal(),
                resource.total(), resource.state());
    }
}
