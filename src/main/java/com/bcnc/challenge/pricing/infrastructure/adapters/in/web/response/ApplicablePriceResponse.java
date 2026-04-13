package com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicablePriceResponse(
        Long productId,
        Long brandId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price
) {
}