package com.bcnc.challenge.pricing.application.ports.out;

import com.bcnc.challenge.pricing.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoadApplicablePricePort {

    Optional<Price> loadApplicablePrice(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId
    );
}