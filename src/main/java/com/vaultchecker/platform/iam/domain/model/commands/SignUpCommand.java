package com.vaultchecker.platform.iam.domain.model.commands;

/**
 * Command to register a new user.
 *
 * @param email    the email of the user
 * @param password the raw password of the user
 * @param roleName the role name to assign (e.g. {@code ROLE_STORE_ADMIN}); when {@code null}
 *                 or blank the application service assigns the default {@code ROLE_CUSTOMER}
 */
public record SignUpCommand(String email, String password, String roleName) {
}
