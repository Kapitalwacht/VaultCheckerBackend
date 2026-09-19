package com.vaultchecker.platform.subscriptions.application.commandservices;

import com.vaultchecker.platform.subscriptions.domain.model.aggregates.Subscription;
import com.vaultchecker.platform.subscriptions.domain.model.commands.ChangePlanCommand;

import java.util.Optional;

public interface BillingCommandService {
    Optional<Subscription> handle(ChangePlanCommand command);
}
