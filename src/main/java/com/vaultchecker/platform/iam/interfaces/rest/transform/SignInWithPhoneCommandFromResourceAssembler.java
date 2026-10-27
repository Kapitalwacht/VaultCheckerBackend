package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.SignInWithPhoneCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.PhoneSignInResource;

public class SignInWithPhoneCommandFromResourceAssembler {
    public static SignInWithPhoneCommand toCommandFromResource(PhoneSignInResource resource) {
        return new SignInWithPhoneCommand(resource.phone(), resource.code());
    }
}
