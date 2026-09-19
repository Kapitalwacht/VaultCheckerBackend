package com.vaultchecker.platform.credit.domain.repositories;

import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;

import java.util.List;

public interface InstallmentPaymentRepository {
    InstallmentPayment save(InstallmentPayment payment);

    List<InstallmentPayment> findAll();

    List<InstallmentPayment> findAllByStoreId(String storeId);

    List<InstallmentPayment> findAllByCustomerId(String customerId);

    List<InstallmentPayment> findAllByPurchaseId(String purchaseId);
}
