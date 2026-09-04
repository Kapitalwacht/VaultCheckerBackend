package com.vaultchecker.platform.iam.application.internal.outboundservices.hashing;

/**
 * Outbound port for password hashing operations required by the IAM application layer.
 */
public interface HashingService {
    /**
     * Encodes a raw password for persistence.
     */
    String encode(CharSequence rawPassword);

    /**
     * Verifies whether a raw password matches an encoded password.
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
