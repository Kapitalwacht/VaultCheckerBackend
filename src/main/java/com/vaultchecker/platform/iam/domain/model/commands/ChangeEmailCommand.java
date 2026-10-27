package com.vaultchecker.platform.iam.domain.model.commands;

public record ChangeEmailCommand(String currentEmail, String newEmail) {
}
