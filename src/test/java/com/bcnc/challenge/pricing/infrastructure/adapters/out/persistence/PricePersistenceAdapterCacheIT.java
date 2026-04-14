package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence;

import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.BrandEntity;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.CurrencyEntity;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.PriceEntity;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.bcnc.challenge.pricing.infrastructure.config.CacheConfig.APPLICABLE_PRICE_CACHE;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;


@SpringBootTest
@EnableCaching
class PricePersistenceAdapterCacheIT {

    @Autowired
    private PricePersistenceAdapter pricePersistenceAdapter;

    @MockBean
    private PriceJpaRepository priceJpaRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Cache cache = cacheManager.getCache(APPLICABLE_PRICE_CACHE);
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void shouldUseCacheForRepeatedCallsWithSameParameters() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        Long productId = 35455L;
        Long brandId = 1L;

        PriceEntity entity = buildPriceEntity(
                10L,
                brandId,
                "ZARA",
                productId,
                "PRODUCT-35455",
                2,
                1,
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30),
                new BigDecimal("25.45"),
                "EUR",
                "Euro",
                true,
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 15, 0)
        );

        when(priceJpaRepository.findApplicablePrice(productId, brandId, applicationDate))
                .thenReturn(Optional.of(entity));

        pricePersistenceAdapter.loadApplicablePrice(applicationDate, productId, brandId);
        pricePersistenceAdapter.loadApplicablePrice(applicationDate, productId, brandId);

        verify(priceJpaRepository, times(1))
                .findApplicablePrice(productId, brandId, applicationDate);
    }

    private PriceEntity buildPriceEntity(
            Long id,
            Long brandId,
            String brandName,
            Long productId,
            String productName,
            Integer priceList,
            Integer priority,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal amount,
            String currencyCode,
            String currencyDescription,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        BrandEntity brand = mock(BrandEntity.class);
        when(brand.getId()).thenReturn(brandId);
        when(brand.getName()).thenReturn(brandName);

        ProductEntity product = mock(ProductEntity.class);
        when(product.getId()).thenReturn(productId);
        when(product.getName()).thenReturn(productName);

        CurrencyEntity currency = mock(CurrencyEntity.class);
        when(currency.getIsoCode()).thenReturn(currencyCode);
        when(currency.getDescription()).thenReturn(currencyDescription);

        PriceEntity entity = mock(PriceEntity.class);
        when(entity.getId()).thenReturn(id);
        when(entity.getBrand()).thenReturn(brand);
        when(entity.getProduct()).thenReturn(product);
        when(entity.getPriceList()).thenReturn(priceList);
        when(entity.getPriority()).thenReturn(priority);
        when(entity.getStartDate()).thenReturn(startDate);
        when(entity.getEndDate()).thenReturn(endDate);
        when(entity.getAmount()).thenReturn(amount);
        when(entity.getCurrency()).thenReturn(currency);
        when(entity.isActive()).thenReturn(active);
        when(entity.getCreatedAt()).thenReturn(createdAt);
        when(entity.getUpdatedAt()).thenReturn(updatedAt);

        return entity;
    }
}