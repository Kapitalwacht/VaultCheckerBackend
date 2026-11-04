package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.ResendVerificationCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.ResendVerificationResource;

public class ResendVerificationCommandFromResourceAssembler {
    public static ResendVerificationCommand toCommandFromResource(ResendVerificationResource resource) {
        return new ResendVerificationCommand(resource.email());
    }
}
