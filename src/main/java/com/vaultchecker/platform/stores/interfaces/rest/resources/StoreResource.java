package com.vaultchecker.platform.stores.interfaces.rest.resources;

public record StoreResource(Long id, String storeId, String ruc, String businessName, String category,
                            String address, String phone, String email, String description, String state) {
}
