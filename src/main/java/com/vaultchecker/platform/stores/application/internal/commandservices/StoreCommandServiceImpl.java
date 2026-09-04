package com.vaultchecker.platform.stores.application.internal.commandservices;

import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import com.vaultchecker.platform.stores.application.commandservices.StoreCommandService;
import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.domain.model.commands.CreateStoreCommand;
import com.vaultchecker.platform.stores.domain.model.commands.DeactivateStoreCommand;
import com.vaultchecker.platform.stores.domain.model.commands.UpdateStoreCommand;
import com.vaultchecker.platform.stores.domain.repositories.StoreRepository;
import org.springframework.stereotype.Service;

/**
 * Store command service implementation.
 */
@Service
public class StoreCommandServiceImpl implements StoreCommandService {

    private final StoreRepository storeRepository;

    public StoreCommandServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public Result<Store, ApplicationError> handle(CreateStoreCommand command) {
        if (command.ruc() != null && !command.ruc().isBlank() && storeRepository.existsByRuc(command.ruc())) {
            return Result.failure(ApplicationError.conflict("Store", "A store with the same RUC already exists"));
        }
        if (command.storeId() != null && !command.storeId().isBlank() && storeRepository.existsByStoreId(command.storeId())) {
            return Result.failure(ApplicationError.conflict("Store", "A store with the same storeId already exists"));
        }
        var store = new Store(command.storeId(), command.ruc(), command.businessName(), command.category(),
                command.address(), command.phone(), command.email(), command.description());
        return Result.success(storeRepository.save(store));
    }

    @Override
    public Result<Store, ApplicationError> handle(UpdateStoreCommand command) {
        var existing = storeRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Store", String.valueOf(command.id())));
        }
        var store = existing.get();
        store.setStoreId(command.storeId());
        store.setRuc(command.ruc());
        store.setBusinessName(command.businessName());
        store.setCategory(command.category());
        store.setAddress(command.address());
        store.setPhone(command.phone());
        store.setEmail(command.email());
        store.setDescription(command.description());
        if (command.state() != null && !command.state().isBlank()) {
            store.setState(command.state());
        }
        return Result.success(storeRepository.save(store));
    }

    @Override
    public Result<Store, ApplicationError> handle(DeactivateStoreCommand command) {
        var existing = storeRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Store", String.valueOf(command.id())));
        }
        var store = existing.get();
        store.deactivate();
        return Result.success(storeRepository.save(store));
    }
}
