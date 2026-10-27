package com.vaultchecker.platform.iam.domain.model.commands;

public record ChangePasswordCommand(String email, String currentPassword, String newPassword) {
}
