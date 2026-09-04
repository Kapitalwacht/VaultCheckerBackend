package com.vaultchecker.platform.catalog.interfaces.rest.transform;

import com.vaultchecker.platform.catalog.domain.model.commands.CreateProductCommand;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.CreateProductResource;

/**
 * Assembler that translates {@link CreateProductResource} into {@link CreateProductCommand}.
 */
public class CreateProductCommandFromResourceAssembler {
    public static CreateProductCommand toCommandFromResource(CreateProductResource resource) {
        return new CreateProductCommand(resource.productId(), resource.storeId(), resource.name(),
                resource.category(), resource.unit(), resource.price(), resource.stock());
    }
}
