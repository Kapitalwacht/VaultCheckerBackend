package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.RefreshSessionCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.RefreshTokenResource;

public class RefreshSessionCommandFromResourceAssembler {
    public static RefreshSessionCommand toCommandFromResource(RefreshTokenResource resource) {
        return new RefreshSessionCommand(resource.refreshToken());
    }
}
