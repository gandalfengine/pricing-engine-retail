package com.bcnc.challenge.pricing.application.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicablePriceResult(
        Long productId,
        Long brandId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price
) {}

