package com.vaultchecker.platform.iam.application.queryservices;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.vaultchecker.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.vaultchecker.platform.iam.domain.model.queries.GetUserByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for IAM user read queries.
 */
public interface UserQueryService {
    List<User> handle(GetAllUsersQuery query);

    Optional<User> handle(GetUserByIdQuery query);

    Optional<User> handle(GetUserByEmailQuery query);
}
