package com.vaultchecker.platform.catalog.application.commandservices;

import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.domain.model.commands.CreateProductCommand;
import com.vaultchecker.platform.catalog.domain.model.commands.DeleteProductCommand;
import com.vaultchecker.platform.catalog.domain.model.commands.UpdateProductCommand;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;

/**
 * Application service contract for product commands.
 */
public interface ProductCommandService {
    Result<Product, ApplicationError> handle(CreateProductCommand command);

    Result<Product, ApplicationError> handle(UpdateProductCommand command);

    Result<Boolean, ApplicationError> handle(DeleteProductCommand command);
}
