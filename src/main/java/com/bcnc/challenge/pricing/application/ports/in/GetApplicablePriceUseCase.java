package com.bcnc.challenge.pricing.application.ports.in;

import com.bcnc.challenge.pricing.application.result.ApplicablePriceResult;

import java.time.LocalDateTime;

public interface GetApplicablePriceUseCase {
    ApplicablePriceResult execute(LocalDateTime applicationDate, Long productId, Long brandId);
}