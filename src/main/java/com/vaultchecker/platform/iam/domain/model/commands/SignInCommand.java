package com.vaultchecker.platform.iam.domain.model.commands;

/**
 * Command to sign in a user.
 *
 * @param email    the email of the user
 * @param password the raw password of the user
 */
public record SignInCommand(String email, String password) {
}
