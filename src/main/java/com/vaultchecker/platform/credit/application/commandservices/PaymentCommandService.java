package com.vaultchecker.platform.credit.application.commandservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.Payment;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterPaymentCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;

public interface PaymentCommandService {
    Result<Payment, ApplicationError> handle(RegisterPaymentCommand command);
}
