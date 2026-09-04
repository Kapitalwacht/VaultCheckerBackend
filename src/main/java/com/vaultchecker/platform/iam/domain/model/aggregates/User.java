package com.vaultchecker.platform.iam.domain.model.aggregates;

import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User aggregate root. A user authenticates against the platform and carries one or more roles.
 */
@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    @Setter
    private Long id;
    @Setter
    private String email;
    @Setter
    private String password;
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

    /**
     * Add a role to the user.
     */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    /**
     * Add a list of roles to the user.
     */
    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }
}
