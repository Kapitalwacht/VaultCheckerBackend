package com.vaultchecker.platform.credit.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterPurchaseCommand(String purchaseId, String storeId, String customerId, String productId,
                                      String description, Integer quantity, BigDecimal amount, LocalDate purchaseDate,
                                      Integer months, String state) {
}
