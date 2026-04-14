package com.bcnc.challenge.pricing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

public record Price(
        Long id,
        Brand brand,
        Product product,
        Integer priceList,
        Integer priority,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal amount,
        Currency currency,
        AuditMetadata auditMetadata
) {
    public Price {
        requireNonNull(brand, "brand must not be null");
        requireNonNull(product, "product must not be null");
        requireNonNull(priceList, "priceList must not be null");
        requireNonNull(priority, "priority must not be null");
        requireNonNull(startDate, "startDate must not be null");
        requireNonNull(endDate, "endDate must not be null");
        requireNonNull(amount, "amount must not be null");
        requireNonNull(currency, "currency must not be null");
        requireNonNull(auditMetadata, "auditMetadata must not be null");


        if (startDate.isAfter(endDate)) {
            throw new IllegalStateException("startDate must be before or equal to endDate");
        }
    }
}