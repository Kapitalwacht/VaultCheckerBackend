package com.vaultchecker.platform.iam.infrastructure.persistence.jpa.adapters;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.repositories.UserRepository;
import com.vaultchecker.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import com.vaultchecker.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final int MIN_PHONE_DIGITS = 9;

    private final UserPersistenceRepository userPersistenceRepository;

    public UserRepositoryImpl(UserPersistenceRepository userPersistenceRepository) {
        this.userPersistenceRepository = userPersistenceRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userPersistenceRepository.findById(id).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userPersistenceRepository.findByEmail(email).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        var digits = digitsOf(phone);
        if (digits.length() < MIN_PHONE_DIGITS) return Optional.empty();
        return userPersistenceRepository.findAll().stream()
                .filter(entity -> samePhone(digits, digitsOf(entity.getPhone())))
                .findFirst()
                .map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    private static boolean samePhone(String a, String b) {
        if (b.length() < MIN_PHONE_DIGITS) return false;
        return a.length() >= b.length() ? a.endsWith(b) : b.endsWith(a);
    }

    @Override
    public List<User> findAll() {
        return userPersistenceRepository.findAll().stream().map(UserPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public User save(User user) {
        var saved = userPersistenceRepository.save(UserPersistenceAssembler.toPersistenceFromDomain(user));
        return UserPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userPersistenceRepository.existsByEmail(email);
    }

    private static String digitsOf(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }
}
