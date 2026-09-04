package com.vaultchecker.platform.stores.interfaces.rest.transform;

import com.vaultchecker.platform.stores.domain.model.commands.UpdateStoreCommand;
import com.vaultchecker.platform.stores.interfaces.rest.resources.UpdateStoreResource;

/**
 * Assembler that translates {@link UpdateStoreResource} into {@link UpdateStoreCommand}.
 */
public class UpdateStoreCommandFromResourceAssembler {
    public static UpdateStoreCommand toCommandFromResource(Long id, UpdateStoreResource resource) {
        return new UpdateStoreCommand(id, resource.storeId(), resource.ruc(), resource.businessName(), resource.category(),
                resource.address(), resource.phone(), resource.email(), resource.description(), resource.state());
    }
}
