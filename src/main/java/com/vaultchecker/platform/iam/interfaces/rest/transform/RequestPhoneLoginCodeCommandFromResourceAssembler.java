package com.vaultchecker.platform.iam.interfaces.rest.transform;

import com.vaultchecker.platform.iam.domain.model.commands.RequestPhoneLoginCodeCommand;
import com.vaultchecker.platform.iam.interfaces.rest.resources.PhoneLoginCodeRequestResource;

public class RequestPhoneLoginCodeCommandFromResourceAssembler {
    public static RequestPhoneLoginCodeCommand toCommandFromResource(PhoneLoginCodeRequestResource resource) {
        return new RequestPhoneLoginCodeCommand(resource.phone());
    }
}
