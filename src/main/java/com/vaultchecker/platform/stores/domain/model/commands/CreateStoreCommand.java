package com.vaultchecker.platform.stores.domain.model.commands;

/**
 * Command to register a new store on the platform (US-01).
 */
public record CreateStoreCommand(String storeId, String ruc, String businessName, String category,
                                 String address, String phone, String email, String description) {
}
