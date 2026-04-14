package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PriceEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistPriceEntitySuccessfully() {
        BrandEntity brand = entityManager.find(BrandEntity.class, 1L);
        ProductEntity product = entityManager.find(ProductEntity.class, 35455L);
        CurrencyEntity currency = entityManager.find(CurrencyEntity.class, "EUR");

        assertNotNull(brand);
        assertNotNull(product);
        assertNotNull(currency);

        PriceEntity price = new PriceEntity(
                null,
                brand,
                product,
                100,
                5,
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 12, 31, 23, 59, 59),
                new BigDecimal("99.99"),
                currency
        );

        entityManager.persist(price);
        entityManager.flush();
        entityManager.clear();

        PriceEntity saved = entityManager.find(PriceEntity.class, price.getId());

        assertNotNull(saved);
        assertEquals(100, saved.getPriceList());
        assertEquals(5, saved.getPriority());
        assertEquals(new BigDecimal("99.99"), saved.getAmount());
        assertEquals("ZARA", saved.getBrand().getName());
        assertEquals("PRODUCT-35455", saved.getProduct().getName());
        assertEquals("EUR", saved.getCurrency().getIsoCode());
    }

    @Test
    void shouldFailWhenMandatoryFieldsAreNull() {
        BrandEntity brand = entityManager.find(BrandEntity.class, 1L);
        ProductEntity product = entityManager.find(ProductEntity.class, 35455L);
        CurrencyEntity currency = entityManager.find(CurrencyEntity.class, "EUR");

        assertNotNull(brand);
        assertNotNull(product);
        assertNotNull(currency);

        PriceEntity price = new PriceEntity(
                null,
                brand,
                product,
                null,
                0,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                new BigDecimal("10.00"),
                currency
        );

        assertThrows(Exception.class, () -> {
            entityManager.persist(price);
            entityManager.flush();
        });
    }

    @Test
    void shouldValidateRelationships() {
        BrandEntity brand = entityManager.find(BrandEntity.class, 2L);
        ProductEntity product = entityManager.find(ProductEntity.class, 35456L);
        CurrencyEntity currency = entityManager.find(CurrencyEntity.class, "USD");

        assertNotNull(brand);
        assertNotNull(product);
        assertNotNull(currency);

        PriceEntity price = new PriceEntity(
                null,
                brand,
                product,
                200,
                1,
                LocalDateTime.of(2026, 2, 1, 0, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59),
                new BigDecimal("20.00"),
                currency
        );

        entityManager.persist(price);
        entityManager.flush();
        entityManager.clear();

        PriceEntity found = entityManager.find(PriceEntity.class, price.getId());

        assertNotNull(found);
        assertEquals("PULL_AND_BEAR", found.getBrand().getName());
        assertEquals("PRODUCT-35456", found.getProduct().getName());
        assertEquals("USD", found.getCurrency().getIsoCode());
    }
}