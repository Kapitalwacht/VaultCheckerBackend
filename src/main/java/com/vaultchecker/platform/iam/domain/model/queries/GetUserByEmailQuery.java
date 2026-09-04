package com.vaultchecker.platform.iam.domain.model.queries;

/**
 * Query to get a user by its email.
 *
 * @param email the email of the user
 */
public record GetUserByEmailQuery(String email) {
}
