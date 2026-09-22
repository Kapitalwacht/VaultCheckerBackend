package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.iam.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}
