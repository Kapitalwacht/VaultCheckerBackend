package com.vaultchecker.platform.iam.domain.model.commands;

public record SignUpCommand(String email, String password, String roleName,
                            String name, String phone, String storeId) {
}
