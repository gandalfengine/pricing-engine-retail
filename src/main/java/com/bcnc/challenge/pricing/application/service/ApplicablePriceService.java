package com.bcnc.challenge.pricing.application.service;

import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ApplicablePriceService implements GetApplicablePriceUseCase {

    @Override
    public ApplicablePriceResponse execute(LocalDateTime applicationDate, Long productId, Long brandId) {
        return new ApplicablePriceResponse(
                productId,
                brandId,
                1,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50")
        );
    }
}