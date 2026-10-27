package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.ChangeEmailCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.ChangeEmailResource;

public class ChangeEmailCommandFromResourceAssembler {
    public static ChangeEmailCommand toCommandFromResource(String currentEmail, ChangeEmailResource resource) {
        return new ChangeEmailCommand(currentEmail, resource.email());
    }
}
