package com.bcnc.challenge.pricing.application.ports.in;

import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;

import java.time.LocalDateTime;

public interface GetApplicablePriceUseCase {
    ApplicablePriceResponse execute(LocalDateTime applicationDate, Long productId, Long brandId);
}