package com.vaultchecker.platform.iam.domain.model.valueobjects;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;

public record AuthenticatedSession(User user, String token, String refreshToken) {
}
