package com.vaultchecker.platform.credit.domain.repositories;

import com.vaultchecker.platform.credit.domain.model.aggregates.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);

    List<Payment> findAllByCreditAccountId(Long creditAccountId);
}
