package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "currencies")
public class CurrencyEntity extends AuditableEntity{

    @Id
    @Column(name = "iso_code", nullable = false, length = 3)
    private String isoCode;

    @Column(name = "description", nullable = false, length = 50)
    private String description;

    protected CurrencyEntity() {
    }

    public CurrencyEntity(String isoCode, String description) {
        this.isoCode = isoCode;
        this.description = description;
    }

}