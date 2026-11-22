package com.vaultchecker.platform.catalog.application.internal.commandservices;

import com.vaultchecker.platform.shared.application.audit.AuditTrail;
import com.vaultchecker.platform.catalog.application.commandservices.ProductCommandService;
import com.vaultchecker.platform.catalog.domain.model.aggregates.Product;
import com.vaultchecker.platform.catalog.domain.model.commands.CreateProductCommand;
import com.vaultchecker.platform.catalog.domain.model.commands.DeleteProductCommand;
import com.vaultchecker.platform.catalog.domain.model.commands.UpdateProductCommand;
import com.vaultchecker.platform.catalog.domain.repositories.ProductRepository;
import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final AuditTrail auditTrail;

    public ProductCommandServiceImpl(ProductRepository productRepository, AuditTrail auditTrail) {
        this.productRepository = productRepository;
        this.auditTrail = auditTrail;
    }

    @Override
    public Result<Product, ApplicationError> handle(CreateProductCommand command) {
        if (command.name() == null || command.name().isBlank()) {
            return Result.failure(ApplicationError.validationError("name", "error.detail.product-name-required"));
        }
        var product = new Product(command.productId(), command.storeId(), command.name(), command.category(),
                command.brand(), command.supplier(), command.unit(), command.cashPrice(), command.listPrice(), command.paymentMode(),
                command.imageUrl());
        var saved = productRepository.save(product);
        auditTrail.record("CREATE_PRODUCT", "Product %s created".formatted(saved.getName()), saved.getStoreId());
        return Result.success(saved);
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
        product.setBrand(command.brand());
        product.setSupplier(command.supplier());
        product.setUnit(command.unit());
        product.setCashPrice(command.cashPrice());
        product.setListPrice(command.listPrice());
        product.setPaymentMode(command.paymentMode());
        product.setImageUrl(command.imageUrl());
        if (command.state() != null && !command.state().isBlank()) {
            product.setState(command.state());
        }
        var saved = productRepository.save(product);
        auditTrail.record("UPDATE_PRODUCT", "Product %s updated".formatted(saved.getName()), saved.getStoreId());
        return Result.success(saved);
    }

    @Override
    public Result<Boolean, ApplicationError> handle(DeleteProductCommand command) {
        var existing = productRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Product", String.valueOf(command.id())));
        }
        productRepository.deleteById(command.id());
        auditTrail.record("DELETE_PRODUCT", "Product %s deleted".formatted(existing.get().getName()), existing.get().getStoreId());
        return Result.success(true);
    }
}
