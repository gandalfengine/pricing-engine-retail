package com.bcnc.challenge.pricing.infrastructure.config;

import java.time.LocalDateTime;

public final class CacheKeyFactory {

    private CacheKeyFactory() {
    }

    public static String applicablePriceKey(LocalDateTime applicationDate, Long productId, Long brandId) {
        return applicationDate + "|" + productId + "|" + brandId;
    }
}
