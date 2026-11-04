package com.vaultchecker.platform.iam.application.internal.outboundservices.stores;

public interface StoreDirectoryService {

    boolean isInactive(String storeId);

    boolean exists(String storeId);

    boolean isRucTaken(String ruc);

    void register(String storeId, String ruc, String businessName, String category, String phone, String email);
}
