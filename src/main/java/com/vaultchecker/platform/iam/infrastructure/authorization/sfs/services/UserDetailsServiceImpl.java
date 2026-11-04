package com.vaultchecker.platform.iam.infrastructure.authorization.sfs.services;

import com.vaultchecker.platform.iam.application.internal.outboundservices.stores.StoreDirectoryService;
import com.vaultchecker.platform.iam.domain.repositories.UserRepository;
import com.vaultchecker.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final StoreDirectoryService storeDirectoryService;

    public UserDetailsServiceImpl(UserRepository userRepository, StoreDirectoryService storeDirectoryService) {
        this.userRepository = userRepository;
        this.storeDirectoryService = storeDirectoryService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        boolean enabled = !user.isStoreAdmin() || !storeDirectoryService.isInactive(user.getStoreId());
        return UserDetailsImpl.build(user, enabled);
    }
}
