package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.valueobjects.RoleNameMapper;
import com.vaultchecker.platform.iam.interfaces.rest.resources.UserResource;

/**
 * Assembler that converts IAM {@link User} aggregates into REST {@link UserResource} objects.
 */
public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User user) {
        var roles = user.getRoles().stream().map(r -> RoleNameMapper.toClientRole(r.getName())).toList();
        return new UserResource(user.getId(), user.getEmail(), user.getName(),
                user.getPhone(), user.getStoreId(), user.isEmailVerified(), roles);
    }
}
