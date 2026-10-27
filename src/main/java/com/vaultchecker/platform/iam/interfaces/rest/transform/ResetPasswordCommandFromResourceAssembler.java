package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.PasswordResetResource;

public class ResetPasswordCommandFromResourceAssembler {
    public static ResetPasswordCommand toCommandFromResource(PasswordResetResource resource) {
        return new ResetPasswordCommand(resource.email(), resource.code(), resource.newPassword());
    }
}
