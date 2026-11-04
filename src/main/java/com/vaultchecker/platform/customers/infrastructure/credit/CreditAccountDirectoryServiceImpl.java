package com.vaultchecker.platform.customers.infrastructure.credit;

import com.vaultchecker.platform.credit.domain.repositories.CreditAccountRepository;
import com.vaultchecker.platform.customers.application.internal.outboundservices.credit.CreditAccountDirectoryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreditAccountDirectoryServiceImpl implements CreditAccountDirectoryService {

    private final CreditAccountRepository creditAccountRepository;

    public CreditAccountDirectoryServiceImpl(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    @Override
    public boolean hasDebt(Long customerId) {
        return creditAccountRepository.findAllByCustomerId(String.valueOf(customerId)).stream()
                .anyMatch(account -> account.getBalance().signum() > 0);
    }

    @Override
    public void updateCreditLimit(Long customerId, BigDecimal creditLimit) {
        creditAccountRepository.findAllByCustomerId(String.valueOf(customerId)).forEach(account -> {
            account.setCreditLimit(creditLimit);
            creditAccountRepository.save(account);
        });
    }
}
