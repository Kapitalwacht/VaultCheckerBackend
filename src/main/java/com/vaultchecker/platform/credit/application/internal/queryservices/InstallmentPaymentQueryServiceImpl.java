package com.vaultchecker.platform.credit.application.internal.queryservices;

import com.vaultchecker.platform.credit.application.queryservices.InstallmentPaymentQueryService;
import com.vaultchecker.platform.credit.domain.model.aggregates.InstallmentPayment;
import com.vaultchecker.platform.credit.domain.model.queries.GetAllInstallmentPaymentsQuery;
import com.vaultchecker.platform.credit.domain.repositories.InstallmentPaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallmentPaymentQueryServiceImpl implements InstallmentPaymentQueryService {

    private final InstallmentPaymentRepository repository;

    public InstallmentPaymentQueryServiceImpl(InstallmentPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<InstallmentPayment> handle(GetAllInstallmentPaymentsQuery query) {
        if (query.purchaseId() != null && !query.purchaseId().isBlank()) {
            return repository.findAllByPurchaseId(query.purchaseId());
        }
        if (query.customerId() != null && !query.customerId().isBlank()) {
            return repository.findAllByCustomerId(query.customerId());
        }
        if (query.storeId() != null && !query.storeId().isBlank()) {
            return repository.findAllByStoreId(query.storeId());
        }
        return repository.findAll();
    }
}
