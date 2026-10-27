package com.vaultchecker.platform.iam.domain.model.aggregates;

import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    @Setter
    private Long id;
    @Setter
    private String email;
    @Setter
    private String password;
    @Setter
    private String name;
    @Setter
    private String phone;
    @Setter
    private String storeId;
    @Setter
    private String customerId;
    @Setter
    private boolean emailVerified;
    @Setter
    private String verificationToken;
    @Setter
    private String recoveryCode;
    @Setter
    private Long recoveryExpiresAt;
    @Setter
    private String loginCode;
    @Setter
    private Long loginCodeExpiresAt;
    @Setter
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public User(String email, String password, List<Role> roles) {
        this(email, password);
        addRoles(roles);
    }

    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }

    public void issueVerificationToken(String token) {
        this.emailVerified = false;
        this.verificationToken = token;
    }

    public boolean confirmEmail(String token) {
        if (verificationToken == null || !verificationToken.equals(token)) return false;
        this.emailVerified = true;
        this.verificationToken = null;
        return true;
    }

    public void changeEmail(String newEmail, String verificationToken) {
        this.email = newEmail;
        issueVerificationToken(verificationToken);
    }

    public void updateProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void issueRecoveryCode(String code, long expiresAt) {
        this.recoveryCode = code;
        this.recoveryExpiresAt = expiresAt;
    }

    public boolean hasRecoveryCode(String code) {
        return recoveryCode != null && recoveryCode.equals(code);
    }

    public boolean isRecoveryCodeExpired(long now) {
        return recoveryExpiresAt != null && now > recoveryExpiresAt;
    }

    public void resetPassword(String encodedPassword) {
        this.password = encodedPassword;
        this.recoveryCode = null;
        this.recoveryExpiresAt = null;
    }

    public void issueLoginCode(String code, long expiresAt) {
        this.loginCode = code;
        this.loginCodeExpiresAt = expiresAt;
    }

    public boolean hasLoginCode(String code) {
        return loginCode != null && loginCode.equals(code);
    }

    public boolean isLoginCodeExpired(long now) {
        return loginCodeExpiresAt != null && now > loginCodeExpiresAt;
    }

    public void clearLoginCode() {
        this.loginCode = null;
        this.loginCodeExpiresAt = null;
    }
}
