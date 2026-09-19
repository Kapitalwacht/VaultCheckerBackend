package com.vaultchecker.platform.catalog.interfaces.rest.transform;

import com.vaultchecker.platform.catalog.domain.model.commands.UpdateProductCommand;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.UpdateProductResource;

public class UpdateProductCommandFromResourceAssembler {
    public static UpdateProductCommand toCommandFromResource(Long id, UpdateProductResource resource) {
        return new UpdateProductCommand(id, resource.productId(), resource.storeId(), resource.name(),
                resource.category(), resource.brand(), resource.unit(), resource.cashPrice(),
                resource.listPrice(), resource.paymentMode(), resource.imageUrl(), resource.state());
    }
}
