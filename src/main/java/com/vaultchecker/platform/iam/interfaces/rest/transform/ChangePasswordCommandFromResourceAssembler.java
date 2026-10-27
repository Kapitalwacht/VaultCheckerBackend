package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.ChangePasswordCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.ChangePasswordResource;

public class ChangePasswordCommandFromResourceAssembler {
    public static ChangePasswordCommand toCommandFromResource(String email, ChangePasswordResource resource) {
        return new ChangePasswordCommand(email, resource.currentPassword(), resource.newPassword());
    }
}
