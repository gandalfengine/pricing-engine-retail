package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence;

import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.BrandEntity;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.CurrencyEntity;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.PriceEntity;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricePersistenceAdapterTest {

    @Mock
    private PriceJpaRepository priceJpaRepository;

    @InjectMocks
    private PricePersistenceAdapter pricePersistenceAdapter;

    @Test
    void shouldReturnMappedDomainPriceWhenRepositoryFindsApplicablePrice() {
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

        when(priceJpaRepository.findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
                productId, brandId, applicationDate, applicationDate
        )).thenReturn(Optional.of(entity));

        Optional<Price> result = pricePersistenceAdapter.loadApplicablePrice(applicationDate, productId, brandId);

        assertTrue(result.isPresent());

        Price price = result.get();
        assertEquals(10L, price.id());
        assertEquals(1L, price.brand().id());
        assertEquals("ZARA", price.brand().name());
        assertEquals(35455L, price.product().id());
        assertEquals("PRODUCT-35455", price.product().name());
        assertEquals(2, price.priceList());
        assertEquals(1, price.priority());
        assertEquals(LocalDateTime.of(2020, 6, 14, 15, 0), price.startDate());
        assertEquals(LocalDateTime.of(2020, 6, 14, 18, 30), price.endDate());
        assertEquals(new BigDecimal("25.45"), price.amount());
        assertEquals("EUR", price.currency().isoCode());
        assertEquals("Euro", price.currency().description());
        assertTrue(price.auditMetadata().active());
        assertEquals(LocalDateTime.of(2020, 6, 14, 15, 0), price.auditMetadata().createdAt());
        assertEquals(LocalDateTime.of(2020, 6, 14, 15, 0), price.auditMetadata().updatedAt());
    }

    @Test
    void shouldReturnEmptyWhenRepositoryDoesNotFindApplicablePrice() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 17, 10, 0);
        Long productId = 99999L;
        Long brandId = 99L;

        when(priceJpaRepository.findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
                productId, brandId, applicationDate, applicationDate
        )).thenReturn(Optional.empty());

        Optional<Price> result = pricePersistenceAdapter.loadApplicablePrice(applicationDate, productId, brandId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCallRepositoryWithSameApplicationDateTwice() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0);
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceJpaRepository.findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
                anyLong(), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        pricePersistenceAdapter.loadApplicablePrice(applicationDate, productId, brandId);

        ArgumentCaptor<Long> productIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> brandIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDateTime> firstDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> secondDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(priceJpaRepository, times(1))
                .findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
                        productIdCaptor.capture(),
                        brandIdCaptor.capture(),
                        firstDateCaptor.capture(),
                        secondDateCaptor.capture()
                );

        assertEquals(productId, productIdCaptor.getValue());
        assertEquals(brandId, brandIdCaptor.getValue());
        assertEquals(applicationDate, firstDateCaptor.getValue());
        assertEquals(applicationDate, secondDateCaptor.getValue());
    }

    @Test
    void shouldMapInactiveAuditMetadataCorrectlyWhenEntityIsInactive() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 16, 12, 0);
        Long productId = 35458L;
        Long brandId = 4L;

        PriceEntity entity = buildPriceEntity(
                23L,
                brandId,
                "BERSHKA",
                productId,
                "PRODUCT-35458",
                23,
                3,
                LocalDateTime.of(2020, 6, 16, 10, 0),
                LocalDateTime.of(2020, 6, 16, 20, 0),
                new BigDecimal("229.90"),
                "BRL",
                "Brazilian Real",
                false,
                LocalDateTime.of(2020, 6, 16, 10, 0),
                LocalDateTime.of(2020, 6, 16, 12, 30)
        );

        when(priceJpaRepository.findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
                productId, brandId, applicationDate, applicationDate
        )).thenReturn(Optional.of(entity));

        Optional<Price> result = pricePersistenceAdapter.loadApplicablePrice(applicationDate, productId, brandId);

        assertTrue(result.isPresent());
        assertFalse(result.get().auditMetadata().active());
        assertEquals(LocalDateTime.of(2020, 6, 16, 10, 0), result.get().auditMetadata().createdAt());
        assertEquals(LocalDateTime.of(2020, 6, 16, 12, 30), result.get().auditMetadata().updatedAt());
    }

    @Test
    void shouldMapDifferentCurrencyAndBrandCorrectly() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        Long productId = 35455L;
        Long brandId = 2L;

        PriceEntity entity = buildPriceEntity(
                6L,
                brandId,
                "PULL_AND_BEAR",
                productId,
                "PRODUCT-35455",
                6,
                1,
                LocalDateTime.of(2020, 6, 14, 10, 0),
                LocalDateTime.of(2020, 6, 14, 20, 0),
                new BigDecimal("37.99"),
                "USD",
                "US Dollar",
                true,
                LocalDateTime.of(2020, 6, 14, 10, 0),
                LocalDateTime.of(2020, 6, 14, 10, 0)
        );

        when(priceJpaRepository.findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
                productId, brandId, applicationDate, applicationDate
        )).thenReturn(Optional.of(entity));

        Optional<Price> result = pricePersistenceAdapter.loadApplicablePrice(applicationDate, productId, brandId);

        assertTrue(result.isPresent());
        assertEquals("PULL_AND_BEAR", result.get().brand().name());
        assertEquals("USD", result.get().currency().isoCode());
        assertEquals("US Dollar", result.get().currency().description());
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