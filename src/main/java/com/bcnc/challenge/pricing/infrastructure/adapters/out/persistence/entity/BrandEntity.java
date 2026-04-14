package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "brands")
public class BrandEntity extends AuditableEntity{

    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected BrandEntity() {
    }

    public BrandEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}