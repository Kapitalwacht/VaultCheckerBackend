package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.UpdateProfileCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.UpdateProfileResource;

public class UpdateProfileCommandFromResourceAssembler {
    public static UpdateProfileCommand toCommandFromResource(String email, UpdateProfileResource resource) {
        return new UpdateProfileCommand(email, resource.name(), resource.phone());
    }
}
