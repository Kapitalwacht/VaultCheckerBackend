package com.vaultchecker.platform.iam.application.internal.outboundservices.customers;

import java.util.Optional;

public interface CustomerDirectoryService {

    Optional<CustomerLink> findByPhone(String phone);

    record CustomerLink(String customerId, String storeId) {
    }
}
