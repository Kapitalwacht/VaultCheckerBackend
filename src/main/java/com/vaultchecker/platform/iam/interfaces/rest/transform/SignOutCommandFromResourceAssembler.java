package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.SignOutCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.RefreshTokenResource;

public class SignOutCommandFromResourceAssembler {
    public static SignOutCommand toCommandFromResource(RefreshTokenResource resource) {
        return new SignOutCommand(resource.refreshToken());
    }
}
