package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

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

    @Disabled
    @Test
    void shouldUpdateUpdatedAtOnUpdate() throws InterruptedException {
        BrandEntity brand = new BrandEntity(201L, "BERSHKA");

        entityManager.persist(brand);
        entityManager.flush();

        LocalDateTime createdAt = brand.getCreatedAt();
        LocalDateTime firstUpdatedAt = brand.getUpdatedAt();

        // força diferença de tempo perceptível
        Thread.sleep(10);

        brand.activate(); // qualquer mudança
        entityManager.merge(brand);
        entityManager.flush();

        LocalDateTime updatedAt = brand.getUpdatedAt();

        assertEquals(createdAt, brand.getCreatedAt());
        assertTrue(updatedAt.isAfter(firstUpdatedAt));
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