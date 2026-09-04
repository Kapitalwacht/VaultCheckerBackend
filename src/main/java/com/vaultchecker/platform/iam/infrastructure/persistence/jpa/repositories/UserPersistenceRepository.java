package com.vaultchecker.platform.iam.infrastructure.persistence.jpa.repositories;

import com.vaultchecker.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for IAM user persistence entities.
 */
@Repository
public interface UserPersistenceRepository extends JpaRepository<UserPersistenceEntity, Long> {

    Optional<UserPersistenceEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
