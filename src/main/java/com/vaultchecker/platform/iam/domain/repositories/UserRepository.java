package com.vaultchecker.platform.iam.domain.repositories;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User user);

    boolean existsByEmail(String email);
}
