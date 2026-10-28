package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.SignInWithGoogleCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.GoogleSignInResource;

public class SignInWithGoogleCommandFromResourceAssembler {
    public static SignInWithGoogleCommand toCommandFromResource(GoogleSignInResource resource) {
        return new SignInWithGoogleCommand(resource.accessToken());
    }
}
