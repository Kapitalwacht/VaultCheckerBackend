package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.SignUpCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.SignUpResource;

/**
 * Assembler that translates {@link SignUpResource} into {@link SignUpCommand}.
 */
public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(resource.email(), resource.password(), resource.role(),
                resource.name(), resource.phone(), resource.storeId());
    }
}
