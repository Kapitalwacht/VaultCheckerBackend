package com.vaultchecker.platform.credit.application.commandservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.commands.RegisterInstallmentPaymentCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;

public interface InstallmentPaymentCommandService {
    Result<InstallmentPayment, ApplicationError> handle(RegisterInstallmentPaymentCommand command);
}
