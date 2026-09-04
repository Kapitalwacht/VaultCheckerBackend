package com.vaultchecker.platform.stores.application.commandservices;

import com.vaultchecker.platform.shared.application.result.ApplicationError;
import com.vaultchecker.platform.shared.application.result.Result;
import com.vaultchecker.platform.stores.domain.model.aggregates.Store;
import com.vaultchecker.platform.stores.domain.model.commands.CreateStoreCommand;
import com.vaultchecker.platform.stores.domain.model.commands.DeactivateStoreCommand;
import com.vaultchecker.platform.stores.domain.model.commands.UpdateStoreCommand;

/**
 * Application service contract for store commands.
 */
public interface StoreCommandService {
    Result<Store, ApplicationError> handle(CreateStoreCommand command);

    Result<Store, ApplicationError> handle(UpdateStoreCommand command);

    Result<Store, ApplicationError> handle(DeactivateStoreCommand command);
}
