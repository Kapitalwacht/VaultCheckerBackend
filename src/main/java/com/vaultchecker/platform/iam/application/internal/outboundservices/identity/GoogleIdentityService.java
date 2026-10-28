package com.vaultchecker.platform.iam.application.internal.outboundservices.identity;

import java.util.Optional;

public interface GoogleIdentityService {
    Optional<String> verifiedEmail(String accessToken);
}
