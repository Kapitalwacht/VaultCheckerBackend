package com.vaultchecker.platform.credit.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Result of applying the payment prelación: a payment is imputed first to late/moratory
 * interest, then to compensatory interest, then to principal (capital).
 *
 * @param toLateInterest         amount applied to moratory (late) interest
 * @param toCompensatoryInterest amount applied to compensatory interest
 * @param toPrincipal            amount applied to principal
 * @param unapplied              leftover amount that could not be applied (e.g. overpayment)
 */
public record PaymentAllocation(BigDecimal toLateInterest, BigDecimal toCompensatoryInterest,
                                BigDecimal toPrincipal, BigDecimal unapplied) {
}
