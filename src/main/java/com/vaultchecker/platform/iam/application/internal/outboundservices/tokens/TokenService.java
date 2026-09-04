package com.vaultchecker.platform.iam.application.internal.outboundservices.tokens;

/**
 * Outbound port for bearer token issuance and validation used by IAM commands and queries.
 */
public interface TokenService {

    /**
     * Generates a token for a username.
     */
    String generateToken(String username);

    /**
     * Extracts the username from a token.
     */
    String getUsernameFromToken(String token);

    /**
     * Validates a token.
     */
    boolean validateToken(String token);
}
