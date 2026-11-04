package com.vaultchecker.platform.iam.infrastructure.authorization.sfs.model;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaultchecker.platform.shared.infrastructure.security.ScopedPrincipal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails, ScopedPrincipal {

    private final String username;
    private final String storeId;
    private final String customerId;
    @JsonIgnore
    private final String password;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String storeId, String customerId, String password, boolean enabled,
                           Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.storeId = storeId;
        this.customerId = customerId;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = enabled;
    }

    public static UserDetailsImpl build(User user, boolean enabled) {
        var authorities = user.getRoles().stream()
                .map(role -> role.getName().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getEmail(),
                user.getStoreId(),
                user.getCustomerId(),
                user.getPassword(),
                enabled,
                authorities);
    }
}
