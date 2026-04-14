package com.bcnc.challenge.pricing.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CacheKeyFactoryTest {

    @Test
    void shouldGenerateCorrectKey() {
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 35455L;
        Long brandId = 1L;

        String key = CacheKeyFactory.applicablePriceKey(date, productId, brandId);

        assertEquals("2020-06-14T10:00|35455|1", key);
    }

    @Test
    void shouldGenerateDifferentKeysForDifferentInputs() {
        LocalDateTime date1 = LocalDateTime.of(2020, 6, 14, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2020, 6, 15, 10, 0);

        String key1 = CacheKeyFactory.applicablePriceKey(date1, 35455L, 1L);
        String key2 = CacheKeyFactory.applicablePriceKey(date2, 35455L, 1L);

        assertNotEquals(key1, key2);
    }

    @Test
    void shouldMaintainConsistentFormat() {
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);

        String key = CacheKeyFactory.applicablePriceKey(date, 1L, 2L);

        assertTrue(key.contains("|"));
        assertEquals(3, key.split("\\|").length);
    }

    @Test
    void shouldHandleNullValuesGracefully() {
        String key = CacheKeyFactory.applicablePriceKey(null, null, null);

        assertEquals("null|null|null", key);
    }
}