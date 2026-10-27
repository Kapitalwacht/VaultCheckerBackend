package com.vaultchecker.platform.iam.domain.model.commands;

public record SignInWithPhoneCommand(String phone, String code) {
}
