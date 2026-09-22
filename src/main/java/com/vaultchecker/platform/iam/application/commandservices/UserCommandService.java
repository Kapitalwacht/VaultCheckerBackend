package com.vaultchecker.platform.iam.application.commandservices;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.commands.SignInCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignUpCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

public interface UserCommandService {

    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command);

    Result<User, ApplicationError> handle(SignUpCommand command);
}
