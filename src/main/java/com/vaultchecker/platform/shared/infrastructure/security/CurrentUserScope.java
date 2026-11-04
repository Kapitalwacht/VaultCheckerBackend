package com.vaultchecker.platform.shared.infrastructure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public final class CurrentUserScope {

    private static final String SYSTEM_ADMIN_AUTHORITY = "ROLE_SYSTEM_ADMIN";
    private static final String CUSTOMER_AUTHORITY = "ROLE_CUSTOMER";
    private static final String NO_STORE = "";

    private CurrentUserScope() {
    }

    public static String storeId() {
        return principal().map(ScopedPrincipal::getStoreId).orElse(null);
    }

    public static String restrictStore(String requestedStoreId) {
        if (hasAuthority(SYSTEM_ADMIN_AUTHORITY)) {
            return requestedStoreId;
        }
        return Objects.requireNonNullElse(storeId(), NO_STORE);
    }

    public static boolean canAccessStore(String storeId) {
        return hasAuthority(SYSTEM_ADMIN_AUTHORITY) || (storeId != null && storeId.equals(storeId()));
    }

    public static void requireStore(String storeId) {
        if (!canAccessStore(storeId)) {
            throw new AccessDeniedException("The resource belongs to another store");
        }
    }

    public static void requireCustomer(String customerId) {
        var ownCustomerId = principal().map(ScopedPrincipal::getCustomerId).orElse(null);
        if (hasAuthority(CUSTOMER_AUTHORITY) && !Objects.equals(customerId, ownCustomerId)) {
            throw new AccessDeniedException("The statement belongs to another customer");
        }
    }

    private static Optional<ScopedPrincipal> principal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof ScopedPrincipal scoped
                ? Optional.of(scoped)
                : Optional.empty();
    }

    private static boolean hasAuthority(String authority) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}
