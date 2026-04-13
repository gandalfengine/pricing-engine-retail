package com.bcnc.challenge.pricing.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    @Test
    void shouldCreatePriceWhenDomainStateIsValid() {
        assertDoesNotThrow(() -> buildPrice(
                new BigDecimal("25.45"),
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30)
        ));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenAmountIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> buildPrice(
                        null,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30)
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenStartDateIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> buildPrice(
                        new BigDecimal("25.45"),
                        null,
                        LocalDateTime.of(2020, 6, 14, 18, 30)
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenEndDateIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> buildPrice(
                        new BigDecimal("25.45"),
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        null
                )
        );
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenStartDateIsAfterEndDate() {
        assertThrows(
                IllegalStateException.class,
                () -> buildPrice(
                        new BigDecimal("25.45"),
                        LocalDateTime.of(2020, 6, 14, 19, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30)
                )
        );
    }

    private Price buildPrice(
            BigDecimal amount,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return new Price(
                1L,
                new Brand(1L, "ZARA"),
                new Product(35455L, "PRODUCT-35455"),
                1,
                0,
                startDate,
                endDate,
                amount,
                new Currency("EUR", "Euro"),
                new AuditMetadata(
                        true,
                        LocalDateTime.of(2020, 6, 14, 0, 0),
                        LocalDateTime.of(2020, 6, 14, 0, 0)
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenBrandIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Price(
                        1L,
                        null,
                        new Product(35455L, "PRODUCT-35455"),
                        1,
                        0,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30),
                        new BigDecimal("25.45"),
                        new Currency("EUR", "Euro"),
                        new AuditMetadata(
                                true,
                                LocalDateTime.of(2020, 6, 14, 0, 0),
                                LocalDateTime.of(2020, 6, 14, 0, 0)
                        )
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenProductIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Price(
                        1L,
                        new Brand(1L, "ZARA"),
                        null,
                        1,
                        0,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30),
                        new BigDecimal("25.45"),
                        new Currency("EUR", "Euro"),
                        new AuditMetadata(
                                true,
                                LocalDateTime.of(2020, 6, 14, 0, 0),
                                LocalDateTime.of(2020, 6, 14, 0, 0)
                        )
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenPriceListIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Price(
                        1L,
                        new Brand(1L, "ZARA"),
                        new Product(35455L, "PRODUCT-35455"),
                        null,
                        0,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30),
                        new BigDecimal("25.45"),
                        new Currency("EUR", "Euro"),
                        new AuditMetadata(
                                true,
                                LocalDateTime.of(2020, 6, 14, 0, 0),
                                LocalDateTime.of(2020, 6, 14, 0, 0)
                        )
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenPriorityIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Price(
                        1L,
                        new Brand(1L, "ZARA"),
                        new Product(35455L, "PRODUCT-35455"),
                        1,
                        null,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30),
                        new BigDecimal("25.45"),
                        new Currency("EUR", "Euro"),
                        new AuditMetadata(
                                true,
                                LocalDateTime.of(2020, 6, 14, 0, 0),
                                LocalDateTime.of(2020, 6, 14, 0, 0)
                        )
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCurrencyIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Price(
                        1L,
                        new Brand(1L, "ZARA"),
                        new Product(35455L, "PRODUCT-35455"),
                        1,
                        0,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30),
                        new BigDecimal("25.45"),
                        null,
                        new AuditMetadata(
                                true,
                                LocalDateTime.of(2020, 6, 14, 0, 0),
                                LocalDateTime.of(2020, 6, 14, 0, 0)
                        )
                )
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenAuditMetadataIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Price(
                        1L,
                        new Brand(1L, "ZARA"),
                        new Product(35455L, "PRODUCT-35455"),
                        1,
                        0,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30),
                        new BigDecimal("25.45"),
                        new Currency("EUR", "Euro"),
                        null
                )
        );
    }

    @Test
    void shouldExposeRecordStateCorrectlyWhenPriceIsValid() {
        BigDecimal amount = new BigDecimal("25.45");
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 15, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 18, 30);

        Price price = buildPrice(amount, start, end);

        assertEquals(1L, price.id());
        assertEquals(1L, price.brand().id());
        assertEquals("ZARA", price.brand().name());
        assertEquals(35455L, price.product().id());
        assertEquals("PRODUCT-35455", price.product().name());
        assertEquals(1, price.priceList());
        assertEquals(0, price.priority());
        assertEquals(start, price.startDate());
        assertEquals(end, price.endDate());
        assertEquals(amount, price.amount());
        assertEquals("EUR", price.currency().isoCode());
        assertEquals("Euro", price.currency().description());
        assertTrue(price.auditMetadata().active());
    }
}