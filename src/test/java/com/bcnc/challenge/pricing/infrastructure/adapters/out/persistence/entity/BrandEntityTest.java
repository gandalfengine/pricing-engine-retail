package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BrandEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistBrandEntitySuccessfully() {
        BrandEntity brand = new BrandEntity(100L, "OYSHO");

        entityManager.persist(brand);
        entityManager.flush();
        entityManager.clear();

        BrandEntity saved = entityManager.find(BrandEntity.class, 100L);

        assertNotNull(saved);
        assertEquals(100L, saved.getId());
        assertEquals("OYSHO", saved.getName());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldFailWhenBrandNameIsNull() {
        BrandEntity brand = new BrandEntity(101L, null);

        assertThrows(Exception.class, () -> {
            entityManager.persist(brand);
            entityManager.flush();
        });
    }

    @Test
    void shouldLoadSeededBrandEntity() {
        BrandEntity brand = entityManager.find(BrandEntity.class, 1L);

        assertNotNull(brand);
        assertEquals(1L, brand.getId());
        assertEquals("ZARA", brand.getName());
        assertTrue(brand.isActive());
        assertNotNull(brand.getCreatedAt());
        assertNotNull(brand.getUpdatedAt());
    }

    @Test
    void shouldUpdateUpdatedAtOnUpdate() throws InterruptedException {
        BrandEntity brand = new BrandEntity(201L, "BERSHKA");

        entityManager.persist(brand);
        entityManager.flush();

        LocalDateTime createdAt = brand.getCreatedAt();
        LocalDateTime firstUpdatedAt = brand.getUpdatedAt();

        Thread.sleep(10);

        brand.deactivate();
        entityManager.flush();

        LocalDateTime updatedAt = brand.getUpdatedAt();

        assertEquals(createdAt, brand.getCreatedAt());
        assertTrue(updatedAt.isAfter(firstUpdatedAt));
        assertFalse(brand.isActive());
    }

    @Test
    void shouldUpdateUpdatedAtWhenEntityStateChanges() throws InterruptedException {
        BrandEntity brand = new BrandEntity(202L, "OYSHO");

        entityManager.persist(brand);
        entityManager.flush();

        LocalDateTime firstUpdatedAt = brand.getUpdatedAt();

        Thread.sleep(10);

        brand.deactivate();
        entityManager.flush();

        LocalDateTime secondUpdatedAt = brand.getUpdatedAt();

        assertTrue(secondUpdatedAt.isAfter(firstUpdatedAt));
        assertFalse(brand.isActive());

        Thread.sleep(10);

        brand.activate();
        entityManager.flush();

        LocalDateTime thirdUpdatedAt = brand.getUpdatedAt();

        assertTrue(thirdUpdatedAt.isAfter(secondUpdatedAt));
        assertTrue(brand.isActive());
    }
}