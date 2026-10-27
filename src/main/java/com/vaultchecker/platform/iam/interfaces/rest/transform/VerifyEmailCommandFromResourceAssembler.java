package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.VerifyEmailResource;

public class VerifyEmailCommandFromResourceAssembler {
    public static VerifyEmailCommand toCommandFromResource(VerifyEmailResource resource) {
        return new VerifyEmailCommand(resource.email(), resource.token());
    }
}
