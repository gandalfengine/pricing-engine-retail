package com.bcnc.challenge.pricing.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import static org.junit.jupiter.api.Assertions.*;

class CacheConfigTest {

    private final CacheConfig cacheConfig = new CacheConfig();

    @Test
    void shouldCreateCaffeineCacheManager() {
        CacheManager cacheManager = cacheConfig.cacheManager();

        assertNotNull(cacheManager);
        assertTrue(cacheManager instanceof CaffeineCacheManager);
    }

    @Test
    void shouldContainApplicablePriceCache() {
        CacheManager cacheManager = cacheConfig.cacheManager();

        Cache cache = cacheManager.getCache(CacheConfig.APPLICABLE_PRICE_CACHE);

        assertNotNull(cache);
    }

    @Test
    void shouldStoreAndRetrieveValuesFromCache() {
        CacheManager cacheManager = cacheConfig.cacheManager();
        Cache cache = cacheManager.getCache(CacheConfig.APPLICABLE_PRICE_CACHE);

        assertNotNull(cache);

        String key = "test-key";
        String value = "test-value";

        cache.put(key, value);

        Cache.ValueWrapper cachedValue = cache.get(key);

        assertNotNull(cachedValue);
        assertEquals(value, cachedValue.get());
    }

    @Test
    void shouldReturnNullForNonExistingKey() {
        CacheManager cacheManager = cacheConfig.cacheManager();
        Cache cache = cacheManager.getCache(CacheConfig.APPLICABLE_PRICE_CACHE);

        assertNotNull(cache);

        Cache.ValueWrapper cachedValue = cache.get("non-existing");

        assertNull(cachedValue);
    }
}