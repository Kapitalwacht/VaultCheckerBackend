package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.credit.domain.model.aggregates.Payment;
import com.vaultchecker.platform.credit.domain.repositories.PaymentRepository;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers.PaymentPersistenceAssembler;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.repositories.PaymentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentPersistenceRepository repository;

    public PaymentRepositoryImpl(PaymentPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        var saved = repository.save(PaymentPersistenceAssembler.toPersistenceFromDomain(payment));
        return PaymentPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public List<Payment> findAllByCreditAccountId(Long creditAccountId) {
        return repository.findAllByCreditAccountId(creditAccountId).stream()
                .map(PaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }
}
