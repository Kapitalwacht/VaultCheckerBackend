package com.vaultchecker.platform.credit.interfaces.rest.transform;

import com.vaultchecker.platform.credit.domain.model.aggregates.CreditAccount;
import com.vaultchecker.platform.credit.interfaces.rest.resources.CreditAccountResource;

/**
 * Assembler that converts a {@link CreditAccount} aggregate into a {@link CreditAccountResource}.
 */
public class CreditAccountResourceFromEntityAssembler {
    public static CreditAccountResource toResourceFromEntity(CreditAccount account) {
        return new CreditAccountResource(account.getId(), account.getCreditId(), account.getStoreId(),
                account.getCustomerId(), account.getBalance(), account.getCreditLimit(),
                account.getDueDate(), account.getState());
    }
}
