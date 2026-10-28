package com.vaultchecker.platform.iam.application.commandservices;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.commands.ChangeEmailCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RequestPhoneLoginCodeCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInWithGoogleCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInWithPhoneCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignUpCommand;
import com.vaultchecker.platform.iam.domain.model.commands.UpdateProfileCommand;
import com.vaultchecker.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

public interface UserCommandService {

    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command);

    Result<User, ApplicationError> handle(SignUpCommand command);

    Result<User, ApplicationError> handle(VerifyEmailCommand command);

    Result<User, ApplicationError> handle(RequestPasswordRecoveryCommand command);

    Result<User, ApplicationError> handle(ResetPasswordCommand command);

    Result<String, ApplicationError> handle(RequestPhoneLoginCodeCommand command);

    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInWithPhoneCommand command);

    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInWithGoogleCommand command);

    Result<User, ApplicationError> handle(UpdateProfileCommand command);

    Result<ImmutablePair<User, String>, ApplicationError> handle(ChangeEmailCommand command);

    Result<User, ApplicationError> handle(ChangePasswordCommand command);
}
