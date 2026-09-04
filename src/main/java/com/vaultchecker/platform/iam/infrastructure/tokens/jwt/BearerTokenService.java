package com.vaultchecker.platform.iam.infrastructure.tokens.jwt;

import com.vaultchecker.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

/**
 * JWT-specific extension of the {@link TokenService} port.
 */
public interface BearerTokenService extends TokenService {

    /**
     * Extracts the JWT token from the HTTP request Authorization header.
     */
    String getBearerTokenFrom(HttpServletRequest request);

    /**
     * Generates a JWT token from an authentication object.
     */
    String generateToken(Authentication authentication);
}
