package com.bcnc.challenge.pricing.application.service;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import com.bcnc.challenge.pricing.application.ports.out.LoadApplicablePricePort;
import com.bcnc.challenge.pricing.domain.model.AuditMetadata;
import com.bcnc.challenge.pricing.domain.model.Brand;
import com.bcnc.challenge.pricing.domain.model.Currency;
import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.domain.model.Product;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicablePriceServiceTelemetryTest {

    @Mock
    private LoadApplicablePricePort loadApplicablePricePort;

    private SimpleMeterRegistry meterRegistry;
    private ApplicablePriceService service;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        service = new ApplicablePriceService(loadApplicablePricePort, meterRegistry);
    }

    @Test
    void shouldIncrementRequestAndFoundCountersWhenPriceIsFound() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, 35455L, 1L))
                .thenReturn(Optional.of(buildPrice()));

        service.execute(applicationDate, 35455L, 1L);

        assertEquals(1.0, meterRegistry.get("pricing.applicable_price.requests").counter().count());
        assertEquals(0.0, meterRegistry.get("pricing.applicable_price.not_found").counter().count());
        assertEquals(0.0, meterRegistry.get("pricing.applicable_price.validation_error").counter().count());

        assertEquals(1L, meterRegistry.get("pricing.applicable_price.execution").timer().count());
        assertTrue(meterRegistry.get("pricing.applicable_price.execution").timer().totalTime(java.util.concurrent.TimeUnit.NANOSECONDS) > 0);
    }

    @Test
    void shouldIncrementRequestAndNotFoundCountersWhenPriceIsNotFound() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        when(loadApplicablePricePort.loadApplicablePrice(applicationDate, 35455L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ApplicablePriceNotFoundException.class,
                () -> service.execute(applicationDate, 35455L, 1L));

        assertEquals(1.0, meterRegistry.get("pricing.applicable_price.requests").counter().count());
        assertEquals(0.0, meterRegistry.get("pricing.applicable_price.found").counter().count());
        assertEquals(1.0, meterRegistry.get("pricing.applicable_price.not_found").counter().count());
        assertEquals(0.0, meterRegistry.get("pricing.applicable_price.validation_error").counter().count());

        assertEquals(1L, meterRegistry.get("pricing.applicable_price.execution").timer().count());
    }

    @Test
    void shouldIncrementValidationErrorCounterWhenInputIsInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute(null, 35455L, 1L));

        assertEquals(1.0, meterRegistry.get("pricing.applicable_price.requests").counter().count());
        assertEquals(0.0, meterRegistry.get("pricing.applicable_price.found").counter().count());
        assertEquals(0.0, meterRegistry.get("pricing.applicable_price.not_found").counter().count());
        assertEquals(1.0, meterRegistry.get("pricing.applicable_price.validation_error").counter().count());

        assertEquals(1L, meterRegistry.get("pricing.applicable_price.execution").timer().count());
    }

    private Price buildPrice() {
        return new Price(
                1L,
                new Brand(1L, "ZARA"),
                new Product(35455L, "PRODUCT-35455"),
                1,
                0,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50"),
                new Currency("EUR", "Euro"),
                new AuditMetadata(true, LocalDateTime.now(), LocalDateTime.now())
        );
    }
}