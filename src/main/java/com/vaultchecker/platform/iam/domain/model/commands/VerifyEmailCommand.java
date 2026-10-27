package com.vaultchecker.platform.iam.domain.model.commands;

public record VerifyEmailCommand(String email, String token) {
}
