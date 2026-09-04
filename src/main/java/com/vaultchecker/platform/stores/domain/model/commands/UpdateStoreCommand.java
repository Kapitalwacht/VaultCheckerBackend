package com.vaultchecker.platform.stores.domain.model.commands;

/**
 * Command to update an existing store's data.
 */
public record UpdateStoreCommand(Long id, String storeId, String ruc, String businessName, String category,
                                 String address, String phone, String email, String description, String state) {
}
