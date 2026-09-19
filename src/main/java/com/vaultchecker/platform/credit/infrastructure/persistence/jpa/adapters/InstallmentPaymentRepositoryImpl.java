package com.vaultchecker.platform.credit.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.assemblers.InstallmentPaymentPersistenceAssembler;
import com.vaultchecker.platform.credit.infrastructure.persistence.jpa.repositories.InstallmentPaymentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InstallmentPaymentRepositoryImpl implements InstallmentPaymentRepository {

    private final InstallmentPaymentPersistenceRepository repository;

    public InstallmentPaymentRepositoryImpl(InstallmentPaymentPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public InstallmentPayment save(InstallmentPayment payment) {
        var saved = repository.save(InstallmentPaymentPersistenceAssembler.toPersistenceFromDomain(payment));
        return InstallmentPaymentPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public List<InstallmentPayment> findAll() {
        return repository.findAll().stream()
                .map(InstallmentPaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<InstallmentPayment> findAllByStoreId(String storeId) {
        return repository.findAllByStoreId(storeId).stream()
                .map(InstallmentPaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<InstallmentPayment> findAllByCustomerId(String customerId) {
        return repository.findAllByCustomerId(customerId).stream()
                .map(InstallmentPaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<InstallmentPayment> findAllByPurchaseId(String purchaseId) {
        return repository.findAllByPurchaseId(purchaseId).stream()
                .map(InstallmentPaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }
}
