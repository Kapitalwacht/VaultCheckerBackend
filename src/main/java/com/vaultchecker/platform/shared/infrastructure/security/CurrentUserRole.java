package com.vaultchecker.platform.shared.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUserRole {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SYSTEM_ACTOR = "system";

    private CurrentUserRole() {
    }

    public static String clientRole() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return SYSTEM_ACTOR;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()).toLowerCase().replace('_', '-'))
                .findFirst()
                .orElse(SYSTEM_ACTOR);
    }
}
