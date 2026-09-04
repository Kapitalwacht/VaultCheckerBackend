package com.vaultchecker.platform.iam.domain.model.queries;

/**
 * Query to get a user by its id.
 *
 * @param userId the id of the user
 */
public record GetUserByIdQuery(Long userId) {
}
