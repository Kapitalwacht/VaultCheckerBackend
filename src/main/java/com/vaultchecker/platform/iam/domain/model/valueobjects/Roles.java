package com.vaultchecker.platform.iam.domain.model.valueobjects;

/**
 * Roles supported by the platform, one per persona described in the project brief:
 * <ul>
 *     <li>{@code ROLE_SYSTEM_ADMIN} — platform administrator: registers and deactivates stores.</li>
 *     <li>{@code ROLE_STORE_ADMIN} — business owner: manages products, customers and credit terms.</li>
 *     <li>{@code ROLE_CUSTOMER} — neighbour customer: checks their own debt and payment plan.</li>
 * </ul>
 */
public enum Roles {
    ROLE_SYSTEM_ADMIN,
    ROLE_STORE_ADMIN,
    ROLE_CUSTOMER
}
