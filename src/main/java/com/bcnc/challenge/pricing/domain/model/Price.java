package com.bcnc.challenge.pricing.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Price(
        Long productId,
        Long brandId,
        Integer priceList,
        Integer priority,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price
) {
    public boolean isApplicableAt(LocalDateTime date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isBefore(endDate) || date.isEqual(endDate));
    }
}