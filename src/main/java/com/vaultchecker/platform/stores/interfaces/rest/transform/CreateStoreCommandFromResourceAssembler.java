package com.vaultchecker.platform.stores.interfaces.rest.transform;

import com.vaultchecker.platform.stores.domain.model.commands.CreateStoreCommand;
import com.vaultchecker.platform.stores.interfaces.rest.resources.CreateStoreResource;

/**
 * Assembler that translates {@link CreateStoreResource} into {@link CreateStoreCommand}.
 */
public class CreateStoreCommandFromResourceAssembler {
    public static CreateStoreCommand toCommandFromResource(CreateStoreResource resource) {
        return new CreateStoreCommand(resource.storeId(), resource.ruc(), resource.businessName(), resource.category(),
                resource.address(), resource.phone(), resource.email(), resource.description());
    }
}
