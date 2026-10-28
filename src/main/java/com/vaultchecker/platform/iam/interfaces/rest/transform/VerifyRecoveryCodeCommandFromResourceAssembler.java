package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.VerifyRecoveryCodeCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.RecoveryCodeVerificationResource;

public class VerifyRecoveryCodeCommandFromResourceAssembler {
    public static VerifyRecoveryCodeCommand toCommandFromResource(RecoveryCodeVerificationResource resource) {
        return new VerifyRecoveryCodeCommand(resource.email(), resource.code());
    }
}
