package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.PasswordRecoveryResource;

public class RequestPasswordRecoveryCommandFromResourceAssembler {
    public static RequestPasswordRecoveryCommand toCommandFromResource(PasswordRecoveryResource resource) {
        return new RequestPasswordRecoveryCommand(resource.email());
    }
}
