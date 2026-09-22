package com.vaultchecker.platform.credit.domain.model.valueobjects;

import java.math.BigDecimal;

public record PaymentAllocation(BigDecimal toLateInterest, BigDecimal toCompensatoryInterest,
                                BigDecimal toPrincipal, BigDecimal unapplied) {
}
