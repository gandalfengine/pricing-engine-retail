package com.bcnc.challenge.pricing.application.service;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import com.bcnc.challenge.pricing.application.ports.out.LoadApplicablePricePort;
import com.bcnc.challenge.pricing.application.result.ApplicablePriceResult;
import com.bcnc.challenge.pricing.domain.model.AuditMetadata;
import com.bcnc.challenge.pricing.domain.model.Brand;
import com.bcnc.challenge.pricing.domain.model.Currency;
import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.domain.model.Product;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicablePriceServiceTest {

    @Mock
    private LoadApplicablePricePort loadApplicablePricePort;

    @InjectMocks
    private ApplicablePriceService applicablePriceService;

    @Test
    void shouldReturnApplicablePriceResponseWhenApplicablePriceExists() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        Long productId = 35455L;
        Long brandId = 1L;

        Price price = buildPrice(
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

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.of(price));

        ApplicablePriceResult response = applicablePriceService.execute(applicationDate, productId, brandId);

        assertNotNull(response);
        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(2, response.priceList());
        assertEquals(LocalDateTime.of(2020, 6, 14, 15, 0), response.startDate());
        assertEquals(LocalDateTime.of(2020, 6, 14, 18, 30), response.endDate());
        assertEquals(new BigDecimal("25.45"), response.price());
    }

    @Test
    void shouldCallLoadApplicablePricePortWithExactSearchCriteria() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0);
        Long productId = 35455L;
        Long brandId = 1L;

        Price price = buildPrice(
                11L,
                brandId,
                "ZARA",
                productId,
                "PRODUCT-35455",
                3,
                1,
                LocalDateTime.of(2020, 6, 15, 0, 0),
                LocalDateTime.of(2020, 6, 15, 11, 0),
                new BigDecimal("30.50"),
                "EUR",
                "Euro",
                true,
                LocalDateTime.of(2020, 6, 15, 0, 0),
                LocalDateTime.of(2020, 6, 15, 0, 0)
        );

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.of(price));

        applicablePriceService.execute(applicationDate, productId, brandId);

        ArgumentCaptor<LocalDateTime> applicationDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Long> productIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> brandIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(loadApplicablePricePort, times(1))
                .loadApplicablePrice(applicationDateCaptor.capture(), productIdCaptor.capture(), brandIdCaptor.capture());

        assertEquals(applicationDate, applicationDateCaptor.getValue());
        assertEquals(productId, productIdCaptor.getValue());
        assertEquals(brandId, brandIdCaptor.getValue());
    }

    @Test
    void shouldMapDomainPriceToApplicablePriceResponseCorrectly() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 16, 12, 0);
        Long productId = 35458L;
        Long brandId = 4L;

        Price price = buildPrice(
                22L,
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
                true,
                LocalDateTime.of(2020, 6, 16, 10, 0),
                LocalDateTime.of(2020, 6, 16, 10, 0)
        );

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.of(price));

        ApplicablePriceResult response = applicablePriceService.execute(applicationDate, productId, brandId);

        assertEquals(35458L, response.productId());
        assertEquals(4L, response.brandId());
        assertEquals(23, response.priceList());
        assertEquals(LocalDateTime.of(2020, 6, 16, 10, 0), response.startDate());
        assertEquals(LocalDateTime.of(2020, 6, 16, 20, 0), response.endDate());
        assertEquals(new BigDecimal("229.90"), response.price());
    }

    @Test
    void shouldThrowApplicablePriceNotFoundExceptionWhenNoApplicablePriceIsFound() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 17, 10, 0);
        Long productId = 99999L;
        Long brandId = 1L;

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.empty());

        ApplicablePriceNotFoundException exception = assertThrows(
                ApplicablePriceNotFoundException.class,
                () -> applicablePriceService.execute(applicationDate, productId, brandId)
        );

        assertNotNull(exception);
    }

    @Test
    void shouldIncludeSearchCriteriaInExceptionMessageWhenPriceIsNotFound() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 17, 10, 0);
        Long productId = 35458L;
        Long brandId = 9L;

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.empty());

        ApplicablePriceNotFoundException exception = assertThrows(
                ApplicablePriceNotFoundException.class,
                () -> applicablePriceService.execute(applicationDate, productId, brandId)
        );

        assertEquals(
                "No applicable price found for productId=35458, brandId=9, applicationDate=2020-06-17T10:00",
                exception.getMessage()
        );
    }

    @Test
    void shouldPreserveBigDecimalValueExactlyWhenMappingResponse() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        Long productId = 35455L;
        Long brandId = 2L;

        Price price = buildPrice(
                6L,
                brandId,
                "PULL_AND_BEAR",
                productId,
                "PRODUCT-35455",
                6,
                1,
                LocalDateTime.of(2020, 6, 14, 10, 0),
                LocalDateTime.of(2020, 6, 14, 20, 0),
                new BigDecimal("37.9900"),
                "USD",
                "US Dollar",
                true,
                LocalDateTime.of(2020, 6, 14, 10, 0),
                LocalDateTime.of(2020, 6, 14, 10, 0)
        );

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.of(price));

        ApplicablePriceResult response = applicablePriceService.execute(applicationDate, productId, brandId);

        assertEquals(new BigDecimal("37.9900"), response.price());
    }

    @Test
    void shouldReturnTariffValidityIntervalInsteadOfApplicationDate() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0);
        Long productId = 35455L;
        Long brandId = 3L;

        Price price = buildPrice(
                8L,
                brandId,
                "MASSIMO_DUTTI",
                productId,
                "PRODUCT-35455",
                8,
                2,
                LocalDateTime.of(2020, 6, 15, 8, 0),
                LocalDateTime.of(2020, 6, 15, 22, 0),
                new BigDecimal("39.90"),
                "GBP",
                "British Pound Sterling",
                true,
                LocalDateTime.of(2020, 6, 15, 8, 0),
                LocalDateTime.of(2020, 6, 15, 8, 0)
        );

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.of(price));

        ApplicablePriceResult response = applicablePriceService.execute(applicationDate, productId, brandId);

        assertEquals(LocalDateTime.of(2020, 6, 15, 8, 0), response.startDate());
        assertEquals(LocalDateTime.of(2020, 6, 15, 22, 0), response.endDate());
        assertEquals(applicationDate, LocalDateTime.of(2020, 6, 15, 10, 0));
    }

    @Test
    void shouldNotExposeAuditMetadataOrCurrencyInResponse() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 16, 12, 0);
        Long productId = 35455L;
        Long brandId = 4L;

        Price price = buildPrice(
                10L,
                brandId,
                "BERSHKA",
                productId,
                "PRODUCT-35455",
                10,
                1,
                LocalDateTime.of(2020, 6, 16, 0, 0),
                LocalDateTime.of(2020, 6, 16, 23, 59, 59),
                new BigDecimal("179.90"),
                "BRL",
                "Brazilian Real",
                false,
                LocalDateTime.of(2020, 6, 16, 0, 0),
                LocalDateTime.of(2020, 6, 16, 8, 30)
        );

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, productId, brandId))
                .thenReturn(Optional.of(price));

        ApplicablePriceResult response = applicablePriceService.execute(applicationDate, productId, brandId);

        assertNotNull(response);
        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(10, response.priceList());
        assertEquals(LocalDateTime.of(2020, 6, 16, 0, 0), response.startDate());
        assertEquals(LocalDateTime.of(2020, 6, 16, 23, 59, 59), response.endDate());
        assertEquals(new BigDecimal("179.90"), response.price());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenApplicationDateIsNull() {
        Long productId = 35455L;
        Long brandId = 1L;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicablePriceService.execute(null, productId, brandId)
        );

        assertEquals("applicationDate must not be null", exception.getMessage());
        verify(loadApplicablePricePort, never()).loadApplicablePrice(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenProductIdIsNull() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long brandId = 1L;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicablePriceService.execute(applicationDate, null, brandId)
        );

        assertEquals("productId must not be null", exception.getMessage());
        verify(loadApplicablePricePort, never()).loadApplicablePrice(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenBrandIdIsNull() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 35455L;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicablePriceService.execute(applicationDate, productId, null)
        );

        assertEquals("brandId must not be null", exception.getMessage());
        verify(loadApplicablePricePort, never()).loadApplicablePrice(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenProductIdIsZeroOrNegative() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long brandId = 1L;

        IllegalArgumentException zeroException = assertThrows(
                IllegalArgumentException.class,
                () -> applicablePriceService.execute(applicationDate, 0L, brandId)
        );
        assertEquals("productId must be greater than zero", zeroException.getMessage());

        IllegalArgumentException negativeException = assertThrows(
                IllegalArgumentException.class,
                () -> applicablePriceService.execute(applicationDate, -1L, brandId)
        );
        assertEquals("productId must be greater than zero", negativeException.getMessage());

        verify(loadApplicablePricePort, never()).loadApplicablePrice(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenBrandIdIsZeroOrNegative() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 35455L;

        IllegalArgumentException zeroException = assertThrows(
                IllegalArgumentException.class,
                () -> applicablePriceService.execute(applicationDate, productId, 0L)
        );
        assertEquals("brandId must be greater than zero", zeroException.getMessage());

        IllegalArgumentException negativeException = assertThrows(
                IllegalArgumentException.class,
                () -> applicablePriceService.execute(applicationDate, productId, -1L)
        );
        assertEquals("brandId must be greater than zero", negativeException.getMessage());

        verify(loadApplicablePricePort, never()).loadApplicablePrice(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong());
    }

    private Price buildPrice(
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
            String currencyIsoCode,
            String currencyDescription,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Price(
                id,
                new Brand(brandId, brandName),
                new Product(productId, productName),
                priceList,
                priority,
                startDate,
                endDate,
                amount,
                new Currency(currencyIsoCode, currencyDescription),
                new AuditMetadata(active, createdAt, updatedAt)
        );
    }
}