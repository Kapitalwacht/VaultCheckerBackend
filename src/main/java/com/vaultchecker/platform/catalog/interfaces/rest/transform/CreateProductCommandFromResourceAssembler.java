package com.vaultchecker.platform.catalog.interfaces.rest.transform;

import com.vaultchecker.platform.catalog.domain.model.commands.CreateProductCommand;
import com.vaultchecker.platform.catalog.interfaces.rest.resources.CreateProductResource;

public class CreateProductCommandFromResourceAssembler {
    public static CreateProductCommand toCommandFromResource(CreateProductResource resource) {
        return new CreateProductCommand(resource.productId(), resource.storeId(), resource.name(),
                resource.category(), resource.brand(), resource.unit(), resource.cashPrice(),
                resource.listPrice(), resource.paymentMode(), resource.imageUrl());
    }
}
