package com.vaultchecker.platform.iam.domain.model.valueobjects;

public final class RoleNameMapper {

    private RoleNameMapper() {
    }

    public static String toClientRole(Roles role) {
        return role.name().substring("ROLE_".length()).toLowerCase().replace('_', '-');
    }

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
