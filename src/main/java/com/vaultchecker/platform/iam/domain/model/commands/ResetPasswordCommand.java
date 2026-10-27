package com.vaultchecker.platform.iam.domain.model.commands;

public record ResetPasswordCommand(String email, String code, String newPassword) {
}
