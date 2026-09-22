package com.vaultchecker.platform.iam.domain.model.events;

public record UserSignedUpEvent(Long userId, String email, String role) {
}
