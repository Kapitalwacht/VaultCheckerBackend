package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.valueobjects.RoleNameMapper;
import com.vaultchecker.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Assembler that translates IAM authentication results into {@link AuthenticatedUserResource}.
 */
public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        var role = user.getRoles().stream()
                .map(r -> RoleNameMapper.toClientRole(r.getName()))
                .findFirst()
                .orElse(null);
        return new AuthenticatedUserResource(user.getId(), user.getId(), user.getEmail(), role,
                user.getName(), user.getPhone(), user.getStoreId(), user.getCustomerId(), token);
    }
}
