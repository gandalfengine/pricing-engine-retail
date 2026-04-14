package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistProductEntitySuccessfully() {
        ProductEntity product = new ProductEntity(99999L, "PRODUCT-99999");

        entityManager.persist(product);
        entityManager.flush();
        entityManager.clear();

        ProductEntity saved = entityManager.find(ProductEntity.class, 99999L);

        assertNotNull(saved);
        assertEquals(99999L, saved.getId());
        assertEquals("PRODUCT-99999", saved.getName());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldFailWhenProductNameIsNull() {
        ProductEntity product = new ProductEntity(99998L, null);

        assertThrows(Exception.class, () -> {
            entityManager.persist(product);
            entityManager.flush();
        });
    }

    @Test
    void shouldLoadSeededProductEntity() {
        ProductEntity product = entityManager.find(ProductEntity.class, 35455L);

        assertNotNull(product);
        assertEquals(35455L, product.getId());
        assertEquals("PRODUCT-35455", product.getName());
        assertTrue(product.isActive());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
    }
}