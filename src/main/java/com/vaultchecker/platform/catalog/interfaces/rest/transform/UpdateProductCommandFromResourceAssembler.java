package com.vaultchecker.platform.catalog.interfaces.rest.transform;

import com.vaultchecker.platform.catalog.domain.model.commands.UpdateProductCommand;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.UpdateProductResource;

/**
 * Assembler that translates {@link UpdateProductResource} into {@link UpdateProductCommand}.
 */
public class UpdateProductCommandFromResourceAssembler {
    public static UpdateProductCommand toCommandFromResource(Long id, UpdateProductResource resource) {
        return new UpdateProductCommand(id, resource.productId(), resource.storeId(), resource.name(),
                resource.category(), resource.unit(), resource.price(), resource.stock(), resource.state());
    }
}
