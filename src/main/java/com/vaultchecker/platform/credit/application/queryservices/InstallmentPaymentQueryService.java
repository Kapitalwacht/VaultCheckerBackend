package com.vaultchecker.platform.credit.application.queryservices;

import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllInstallmentPaymentsQuery;

import java.util.List;

public interface InstallmentPaymentQueryService {
    List<InstallmentPayment> handle(GetAllInstallmentPaymentsQuery query);
}
