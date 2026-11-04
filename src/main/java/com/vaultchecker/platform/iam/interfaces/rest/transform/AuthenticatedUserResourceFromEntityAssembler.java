package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.valueobjects.AuthenticatedSession;
import com.vaultchecker.platform.iam.domain.model.valueobjects.RoleNameMapper;
import com.vaultchecker.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(AuthenticatedSession session) {
        var user = session.user();
        var role = user.getRoles().stream()
                .map(r -> RoleNameMapper.toClientRole(r.getName()))
                .findFirst()
                .orElse(null);
        return new AuthenticatedUserResource(user.getId(), user.getId(), user.getEmail(), role,
                user.getName(), user.getPhone(), user.getStoreId(), user.getCustomerId(), session.token(), session.refreshToken());
    }
}
