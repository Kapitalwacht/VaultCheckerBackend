package com.vaultchecker.platform.iam.domain.model.queries;

import com.vaultchecker.platform.iam.domain.model.valueobjects.Roles;

/**
 * Query to get a role by its name.
 *
 * @param name the role name
 */
public record GetRoleByNameQuery(Roles name) {
}
