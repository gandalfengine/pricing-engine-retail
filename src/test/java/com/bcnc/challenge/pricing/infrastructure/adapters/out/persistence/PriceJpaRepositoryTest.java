package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence;

import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.PriceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PriceJpaRepositoryTest {

    @Autowired
    private PriceJpaRepository priceJpaRepository;

    @Test
    @DisplayName("Should return price list 1 for brand 1 / product 35455 on 2020-06-14T10:00")
    void shouldReturnPriceList1ForOriginalScenarioTest1() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 14, 10, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getPriceList());
        assertEquals(0, result.get().getPriority());
    }

    @Test
    @DisplayName("Should return price list 2 for brand 1 / product 35455 on 2020-06-14T16:00")
    void shouldReturnPriceList2ForOriginalScenarioTest2() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 14, 16, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getPriceList());
        assertEquals(1, result.get().getPriority());
    }

    @Test
    @DisplayName("Should return price list 1 for brand 1 / product 35455 on 2020-06-14T21:00")
    void shouldReturnPriceList1ForOriginalScenarioTest3() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 14, 21, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getPriceList());
        assertEquals(0, result.get().getPriority());
    }

    @Test
    @DisplayName("Should return price list 3 for brand 1 / product 35455 on 2020-06-15T10:00")
    void shouldReturnPriceList3ForOriginalScenarioTest4() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 15, 10, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(3, result.get().getPriceList());
        assertEquals(1, result.get().getPriority());
    }

    @Test
    @DisplayName("Should return price list 4 for brand 1 / product 35455 on 2020-06-16T21:00")
    void shouldReturnPriceList4ForOriginalScenarioTest5() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 16, 21, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(4, result.get().getPriceList());
        assertEquals(1, result.get().getPriority());
    }

    @Test
    @DisplayName("Should return promotional price for brand 2 within USD window")
    void shouldReturnPromotionalPriceForBrand2WithinWindow() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                2L,
                LocalDateTime.of(2020, 6, 14, 16, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(6, result.get().getPriceList());
        assertEquals(1, result.get().getPriority());
        assertEquals("USD", result.get().getCurrency().getIsoCode());
    }

    @Test
    @DisplayName("Should return base price for brand 2 outside promotional USD window")
    void shouldReturnBasePriceForBrand2OutsideWindow() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                2L,
                LocalDateTime.of(2020, 6, 14, 21, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(5, result.get().getPriceList());
        assertEquals(0, result.get().getPriority());
        assertEquals("USD", result.get().getCurrency().getIsoCode());
    }

    @Test
    @DisplayName("Should return higher priority price for brand 3 on overlapped valid period")
    void shouldReturnHigherPriorityPriceForBrand3WithinWindow() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                3L,
                LocalDateTime.of(2020, 6, 15, 10, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(8, result.get().getPriceList());
        assertEquals(2, result.get().getPriority());
        assertEquals("GBP", result.get().getCurrency().getIsoCode());
    }

    @Test
    @DisplayName("Should return product 35456 promotional price for brand 1")
    void shouldReturnPromotionalPriceForProduct35456Brand1() {
        Optional<PriceEntity> result = findApplicablePrice(
                35456L,
                1L,
                LocalDateTime.of(2020, 6, 14, 16, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(12, result.get().getPriceList());
        assertEquals(2, result.get().getPriority());
        assertEquals("EUR", result.get().getCurrency().getIsoCode());
    }

    @Test
    @DisplayName("Should return product 35458 highest priority price for brand 4")
    void shouldReturnHighestPriorityPriceForProduct35458Brand4() {
        Optional<PriceEntity> result = findApplicablePrice(
                35458L,
                4L,
                LocalDateTime.of(2020, 6, 16, 12, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(23, result.get().getPriceList());
        assertEquals(3, result.get().getPriority());
        assertEquals("BRL", result.get().getCurrency().getIsoCode());
    }

    @Test
    @DisplayName("Should return empty when combination of product and brand does not exist")
    void shouldReturnEmptyWhenCombinationDoesNotExist() {
        Optional<PriceEntity> result = findApplicablePrice(
                99999L,
                99L,
                LocalDateTime.of(2020, 6, 14, 10, 0)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should consider startDate as inclusive")
    void shouldConsiderStartDateAsInclusive() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 14, 15, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getPriceList());
    }

    @Test
    @DisplayName("Should consider endDate as inclusive")
    void shouldConsiderEndDateAsInclusive() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 14, 18, 30)
        );

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getPriceList());
    }

    @Test
    @DisplayName("Should return empty when applicationDate is before any valid startDate")
    void shouldReturnEmptyWhenApplicationDateIsBeforeAnyValidWindow() {
        Optional<PriceEntity> result = findApplicablePrice(
                35458L,
                4L,
                LocalDateTime.of(2020, 6, 13, 23, 59, 59)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when applicationDate is after valid endDate for a bounded window")
    void shouldReturnEmptyWhenApplicationDateIsAfterBoundedWindow() {
        Optional<PriceEntity> result = findApplicablePrice(
                35458L,
                2L,
                LocalDateTime.of(2020, 6, 15, 12, 0, 1)
        );

        assertTrue(result.isPresent());
        assertEquals(19, result.get().getPriceList(), "Should fall back to base price after promotional window ends");
    }

    @Test
    @DisplayName("Should ignore inactive prices even when date range and priority match")
    @Sql(statements = {
            "INSERT INTO prices (brand_id, product_id, price_list, priority, start_date, end_date, price, currency_code, active, created_at, updated_at) " +
                    "VALUES (1, 35455, 99, 9, TIMESTAMP '2020-06-14 15:00:00', TIMESTAMP '2020-06-14 18:30:00', 10.00, 'EUR', FALSE, TIMESTAMP '2020-06-14 15:00:00', TIMESTAMP '2020-06-14 15:00:00')"
    })
    void shouldIgnoreInactivePrices() {
        Optional<PriceEntity> result = findApplicablePrice(
                35455L,
                1L,
                LocalDateTime.of(2020, 6, 14, 16, 0)
        );

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getPriceList(), "Inactive higher-priority price must be ignored");
        assertTrue(result.get().isActive());
    }

    private Optional<PriceEntity> findApplicablePrice(
            Long productId,
            Long brandId,
            LocalDateTime applicationDate
    ) {
        return priceJpaRepository
                .findApplicablePrice(
                        productId,
                        brandId,
                        applicationDate
                );
    }
}