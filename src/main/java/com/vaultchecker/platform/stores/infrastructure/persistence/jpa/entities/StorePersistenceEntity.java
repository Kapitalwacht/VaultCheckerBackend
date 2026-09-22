package com.vaultchecker.platform.stores.infrastructure.persistence.jpa.entities;

import com.vaultchecker.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
public class StorePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "store_id", length = 60)
    private String storeId;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(name = "category", length = 80)
    private String category;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "state", nullable = false, length = 20)
    private String state;
}
