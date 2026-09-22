package com.vaultchecker.platform.stores.domain.model.commands;

public record CreateStoreCommand(String storeId, String ruc, String businessName, String category,
                                 String address, String phone, String email, String description) {
}
