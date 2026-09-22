package com.vaultchecker.platform.iam.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password", nullable = false, length = 120)
    private String password;

    @Column(name = "name", length = 120)
    private String name;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "store_id", length = 20)
    private String storeId;

    @Column(name = "customer_id", length = 20)
    private String customerId;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "verification_token", length = 60)
    private String verificationToken;

    @Column(name = "recovery_code", length = 10)
    private String recoveryCode;

    @Column(name = "recovery_expires_at")
    private Long recoveryExpiresAt;

    @Column(name = "login_code", length = 10)
    private String loginCode;

    @Column(name = "login_code_expires_at")
    private Long loginCodeExpiresAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RolePersistenceEntity> roles = new HashSet<>();
}
