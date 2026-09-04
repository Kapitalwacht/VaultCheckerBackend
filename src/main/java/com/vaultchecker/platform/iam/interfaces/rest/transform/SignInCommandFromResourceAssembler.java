package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.SignInCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.SignInResource;

/**
 * Assembler that translates {@link SignInResource} into {@link SignInCommand}.
 */
public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(signInResource.email(), signInResource.password());
    }
}
