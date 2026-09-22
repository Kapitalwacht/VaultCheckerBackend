package com.vaultchecker.platform.iam.domain.model.queries;

import com.vaultchecker.platform.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles name) {
}
