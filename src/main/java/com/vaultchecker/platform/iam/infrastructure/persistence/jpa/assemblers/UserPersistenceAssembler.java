package com.vaultchecker.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Static assembler between IAM user domain and persistence representations.
 */
public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {
    }

    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        if (entity == null) return null;
        var domain = new User();
        domain.setId(entity.getId());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        domain.setRoles(entity.getRoles().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .collect(Collectors.toSet()));
        return domain;
    }

    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        if (user == null) return null;
        var entity = new UserPersistenceEntity();
        // Only set the ID when updating an existing user; leave it null for JPA to generate.
        if (user.getId() != null) {
            entity.setId(user.getId());
        }
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setRoles(user.getRoles() == null
                ? new HashSet<>()
                : user.getRoles().stream()
                .map(RolePersistenceAssembler::toPersistenceFromDomain)
                .collect(Collectors.toSet()));
        return entity;
    }
}
