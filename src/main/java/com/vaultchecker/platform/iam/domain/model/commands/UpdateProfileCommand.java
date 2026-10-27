package com.vaultchecker.platform.iam.domain.model.commands;

public record UpdateProfileCommand(String email, String name, String phone) {
}
