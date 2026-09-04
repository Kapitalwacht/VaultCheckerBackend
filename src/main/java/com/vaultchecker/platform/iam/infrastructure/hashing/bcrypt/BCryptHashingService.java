package com.vaultchecker.platform.iam.infrastructure.hashing.bcrypt;

import com.vaultchecker.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Marker interface combining the application {@link HashingService} port with Spring Security's
 * {@link PasswordEncoder}, so a single bean satisfies both.
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
