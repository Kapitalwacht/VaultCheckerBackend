package com.vaultchecker.platform.iam.application.internal.outboundservices.customers;

import java.util.Optional;

public interface CustomerDirectoryService {

    Optional<CustomerLink> findByDni(String dni);

    record CustomerLink(String customerId, String storeId) {
    }
}
