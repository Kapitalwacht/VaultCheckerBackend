package com.vaultchecker.platform.iam.infrastructure.identity.google;

import com.vaultchecker.platform.iam.application.internal.outboundservices.identity.GoogleIdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.Optional;

@Service
public class GoogleTokenInfoIdentityService implements GoogleIdentityService {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenInfoIdentityService.class);

    private final RestClient restClient = RestClient.create();

    @Value("${google.token-info-url:https://oauth2.googleapis.com/tokeninfo}")
    private String tokenInfoUrl;

    @Value("${google.client-id:}")
    private String clientId;

    @Override
    @SuppressWarnings("unchecked")
    public Optional<String> verifiedEmail(String accessToken) {
        try {
            Map<String, Object> info = restClient.get()
                    .uri(tokenInfoUrl + "?access_token={token}", accessToken)
                    .retrieve()
                    .body(Map.class);
            if (info == null) return Optional.empty();
            var audience = String.valueOf(info.getOrDefault("aud", ""));
            var email = String.valueOf(info.getOrDefault("email", ""));
            var verified = "true".equalsIgnoreCase(String.valueOf(info.get("email_verified")));
            if (clientId.isBlank() || !clientId.equals(audience)) {
                log.warn("Google token audience {} does not match the configured client", audience);
                return Optional.empty();
            }
            if (!verified || email.isBlank()) return Optional.empty();
            return Optional.of(email);
        } catch (RestClientException e) {
            log.warn("Google rejected the access token: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
