package com.bcnc.challenge.pricing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
}