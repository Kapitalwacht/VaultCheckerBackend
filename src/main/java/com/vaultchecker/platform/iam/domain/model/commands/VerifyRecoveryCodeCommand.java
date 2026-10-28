package com.vaultchecker.platform.iam.domain.model.commands;

public record VerifyRecoveryCodeCommand(String email, String code) {
}
