package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CurrencyEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistCurrencyEntitySuccessfully() {
        CurrencyEntity currency = new CurrencyEntity("JPY", "Japanese Yen");

        entityManager.persist(currency);
        entityManager.flush();
        entityManager.clear();

        CurrencyEntity saved = entityManager.find(CurrencyEntity.class, "JPY");

        assertNotNull(saved);
        assertEquals("JPY", saved.getIsoCode());
        assertEquals("Japanese Yen", saved.getDescription());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldFailWhenCurrencyDescriptionIsNull() {
        CurrencyEntity currency = new CurrencyEntity("CHF", null);

        assertThrows(Exception.class, () -> {
            entityManager.persist(currency);
            entityManager.flush();
        });
    }

    @Test
    void shouldLoadSeededCurrencyEntity() {
        CurrencyEntity currency = entityManager.find(CurrencyEntity.class, "EUR");

        assertNotNull(currency);
        assertEquals("EUR", currency.getIsoCode());
        assertEquals("Euro", currency.getDescription());
        assertTrue(currency.isActive());
        assertNotNull(currency.getCreatedAt());
        assertNotNull(currency.getUpdatedAt());
    }
}