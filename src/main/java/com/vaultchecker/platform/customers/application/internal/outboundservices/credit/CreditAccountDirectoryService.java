package com.vaultchecker.platform.customers.application.internal.outboundservices.credit;

import java.math.BigDecimal;

public interface CreditAccountDirectoryService {

    boolean hasDebt(Long customerId);

    void updateCreditLimit(Long customerId, BigDecimal creditLimit);
}
