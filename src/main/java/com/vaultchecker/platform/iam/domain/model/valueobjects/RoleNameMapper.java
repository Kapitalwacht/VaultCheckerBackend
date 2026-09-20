package com.vaultchecker.platform.iam.domain.model.valueobjects;

/**
 * Maps between the internal {@link Roles} enum (e.g. {@code ROLE_STORE_ADMIN}) and the kebab-case role
 * names the frontend uses (e.g. {@code store-admin}). Keeping this mapping in one place ensures the
 * REST contract stays in sync with the VaultChecker frontend.
 */
public final class RoleNameMapper {

    private RoleNameMapper() {
    }

    /** {@code ROLE_STORE_ADMIN} → {@code store-admin}. */
    public static String toClientRole(Roles role) {
        return role.name().substring("ROLE_".length()).toLowerCase().replace('_', '-');
    }

    /** Accepts either a kebab-case name ({@code store-admin}) or the enum name ({@code ROLE_STORE_ADMIN}). */
    public static Roles fromClientRole(String value, Roles fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        var v = value.trim();
        try {
            if (v.startsWith("ROLE_")) {
                return Roles.valueOf(v);
            }
            return Roles.valueOf("ROLE_" + v.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
