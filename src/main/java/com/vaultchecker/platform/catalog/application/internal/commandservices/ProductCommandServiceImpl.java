package com.vaultchecker.platform.catalog.application.internal.commandservices;

import com.vaultchecker.platform.catalog.application.commandservices.ProductCommandService;
import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.domain.model.commands.CreateProductCommand;
import com.vaultchecker.platform.catalog.domain.model.commands.DeleteProductCommand;
import com.vaultchecker.platform.catalog.domain.model.commands.UpdateProductCommand;
import com.vaultchecker.platform.catalog.domain.repositories.ProductRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Product command service implementation.
 */
@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;

    public ProductCommandServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Result<Product, ApplicationError> handle(CreateProductCommand command) {
        if (command.name() == null || command.name().isBlank()) {
            return Result.failure(ApplicationError.validationError("name", "Product name is required"));
        }
        var product = new Product(command.productId(), command.storeId(), command.name(), command.category(),
                command.unit(), command.price(), command.stock());
        return Result.success(productRepository.save(product));
    }

    @Override
    public Result<Product, ApplicationError> handle(UpdateProductCommand command) {
        var existing = productRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Product", String.valueOf(command.id())));
        }
        var product = existing.get();
        product.setProductId(command.productId());
        product.setStoreId(command.storeId());
        product.setName(command.name());
        product.setCategory(command.category());
        product.setUnit(command.unit());
        product.setPrice(command.price());
        product.setStock(command.stock());
        if (command.state() != null && !command.state().isBlank()) {
            product.setState(command.state());
        }
        return Result.success(productRepository.save(product));
    }

    @Override
    public Result<Boolean, ApplicationError> handle(DeleteProductCommand command) {
        if (!productRepository.existsById(command.id())) {
            return Result.failure(ApplicationError.notFound("Product", String.valueOf(command.id())));
        }
        productRepository.deleteById(command.id());
        return Result.success(true);
    }
}
