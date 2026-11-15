package com.vaultchecker.platform.iam.application.internal.eventhandlers;

import com.vaultchecker.platform.iam.domain.model.entities.Role;
import com.vaultchecker.platform.iam.domain.model.valueobjects.Roles;
import com.vaultchecker.platform.iam.domain.repositories.RoleRepository;
import com.vaultchecker.platform.iam.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Grants the system administrator role on startup to the account configured in
 * {@code app.system-admin.email}. The account must already exist (sign up first).
 */
@Service
public class SystemAdminSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemAdminSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final String systemAdminEmail;

    public SystemAdminSeeder(UserRepository userRepository,
                             RoleRepository roleRepository,
                             @Value("${app.system-admin.email:}") String systemAdminEmail) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.systemAdminEmail = systemAdminEmail == null ? "" : systemAdminEmail.trim();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void promoteConfiguredSystemAdmin() {
        if (systemAdminEmail.isBlank()) return;

        var user = userRepository.findByEmail(systemAdminEmail)
                .or(() -> userRepository.findByEmail(systemAdminEmail.toLowerCase()));
        if (user.isEmpty()) {
            LOGGER.info("System admin account not registered yet; it will be promoted on the next start after sign-up");
            return;
        }

        var admin = user.get();
        if (admin.isSystemAdmin() && admin.getRoles().size() == 1) return;

        var role = roleRepository.findByName(Roles.ROLE_SYSTEM_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_SYSTEM_ADMIN)));
        admin.promoteToSystemAdmin(role);
        userRepository.save(admin);
        LOGGER.info("Configured account promoted to system administrator");
    }
}
