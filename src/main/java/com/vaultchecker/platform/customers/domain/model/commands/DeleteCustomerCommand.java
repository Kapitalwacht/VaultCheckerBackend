package com.vaultchecker.platform.customers.domain.model.commands;

/**
 * Command to delete a customer by its identifier.
 */
public record DeleteCustomerCommand(Long id) {
}
