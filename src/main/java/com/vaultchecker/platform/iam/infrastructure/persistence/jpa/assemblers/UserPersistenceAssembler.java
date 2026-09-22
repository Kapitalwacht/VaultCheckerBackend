package com.vaultchecker.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.util.HashSet;
import java.util.stream.Collectors;

public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {
    }

    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        if (entity == null) return null;
        var domain = new User();
        domain.setId(entity.getId());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        domain.setName(entity.getName());
        domain.setPhone(entity.getPhone());
        domain.setStoreId(entity.getStoreId());
        domain.setCustomerId(entity.getCustomerId());
        domain.setEmailVerified(entity.isEmailVerified());
        domain.setVerificationToken(entity.getVerificationToken());
        domain.setRecoveryCode(entity.getRecoveryCode());
        domain.setRecoveryExpiresAt(entity.getRecoveryExpiresAt());
        domain.setLoginCode(entity.getLoginCode());
        domain.setLoginCodeExpiresAt(entity.getLoginCodeExpiresAt());
        domain.setRoles(entity.getRoles().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .collect(Collectors.toSet()));
        return domain;
    }

    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        if (user == null) return null;
        var entity = new UserPersistenceEntity();

        if (user.getId() != null) {
            entity.setId(user.getId());
        }
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setPhone(user.getPhone());
        entity.setStoreId(user.getStoreId());
        entity.setCustomerId(user.getCustomerId());
        entity.setEmailVerified(user.isEmailVerified());
        entity.setVerificationToken(user.getVerificationToken());
        entity.setRecoveryCode(user.getRecoveryCode());
        entity.setRecoveryExpiresAt(user.getRecoveryExpiresAt());
        entity.setLoginCode(user.getLoginCode());
        entity.setLoginCodeExpiresAt(user.getLoginCodeExpiresAt());
        entity.setRoles(user.getRoles() == null
                ? new HashSet<>()
                : user.getRoles().stream()
                .map(RolePersistenceAssembler::toPersistenceFromDomain)
                .collect(Collectors.toSet()));
        return entity;
    }
}
