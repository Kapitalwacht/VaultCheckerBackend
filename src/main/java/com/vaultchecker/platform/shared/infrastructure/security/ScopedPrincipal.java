package com.vaultchecker.platform.shared.infrastructure.security;

public interface ScopedPrincipal {

    String getStoreId();

    String getCustomerId();
}
