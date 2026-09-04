package com.vaultchecker.platform.stores.interfaces.rest.resources;

/**
 * Resource representing a store returned by the REST API. Field names match the VaultChecker frontend.
 */
public record StoreResource(Long id, String storeId, String ruc, String businessName, String category,
                            String address, String phone, String email, String description, String state) {
}
