package com.vaultchecker.platform.iam.application.commandservices;

import com.vaultchecker.platform.iam.domain.model.aggregates.User;
import com.vaultchecker.platform.iam.domain.model.commands.ChangeEmailCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RefreshSessionCommand;
import com.vaultchecker.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ResendVerificationCommand;
import com.vaultchecker.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignInWithGoogleCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignOutCommand;
import com.vaultchecker.platform.iam.domain.model.commands.SignUpCommand;
import com.vaultchecker.platform.iam.domain.model.commands.UpdateProfileCommand;
import com.vaultchecker.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vaultchecker.platform.iam.domain.model.commands.VerifyRecoveryCodeCommand;
import com.vaultchecker.platform.iam.domain.model.valueobjects.AuthenticatedSession;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;

public interface UserCommandService {

    Result<AuthenticatedSession, ApplicationError> handle(SignInCommand command);

    Result<AuthenticatedSession, ApplicationError> handle(RefreshSessionCommand command);

    Result<Boolean, ApplicationError> handle(SignOutCommand command);

    Result<User, ApplicationError> handle(SignUpCommand command);

    Result<User, ApplicationError> handle(VerifyEmailCommand command);

    Result<Boolean, ApplicationError> handle(ResendVerificationCommand command);

    Result<User, ApplicationError> handle(RequestPasswordRecoveryCommand command);

    Result<User, ApplicationError> handle(VerifyRecoveryCodeCommand command);

    Result<User, ApplicationError> handle(ResetPasswordCommand command);

    Result<AuthenticatedSession, ApplicationError> handle(SignInWithGoogleCommand command);

    Result<User, ApplicationError> handle(UpdateProfileCommand command);

    Result<AuthenticatedSession, ApplicationError> handle(ChangeEmailCommand command);

    Result<User, ApplicationError> handle(ChangePasswordCommand command);
}
