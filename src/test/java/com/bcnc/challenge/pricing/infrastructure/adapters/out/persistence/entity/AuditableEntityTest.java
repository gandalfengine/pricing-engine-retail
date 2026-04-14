package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuditableEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSetAuditFieldsOnPersist() {
        BrandEntity brand = new BrandEntity(200L, "STRADIVARIUS");

        entityManager.persist(brand);
        entityManager.flush();

        assertTrue(brand.isActive());
        assertNotNull(brand.getCreatedAt());
        assertNotNull(brand.getUpdatedAt());
        assertEquals(brand.getCreatedAt(), brand.getUpdatedAt());
    }

    @Test
    void shouldDeactivateAndActivateEntity() {
        BrandEntity brand = new BrandEntity(202L, "OYSHO");

        entityManager.persist(brand);
        entityManager.flush();

        assertTrue(brand.isActive());

        brand.deactivate();
        assertFalse(brand.isActive());

        brand.activate();
        assertTrue(brand.isActive());
    }
}