package com.vaultchecker.platform.iam.application.queryservices;

import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.vaultchecker.platform.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for IAM role read queries.
 */
public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);

    Optional<Role> handle(GetRoleByNameQuery query);
}
