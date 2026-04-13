package com.bcnc.challenge.pricing.infrastructure.adapters.in.web;

import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/prices")
public class PriceQueryController {

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;

    public PriceQueryController(GetApplicablePriceUseCase getApplicablePriceUseCase) {
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
    }

    @GetMapping
    public ApplicablePriceResponse getApplicablePrice(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime applicationDate,
            @RequestParam Long productId,
            @RequestParam Long brandId
    ) {
        log.info("Received price query request. applicationDate={}, productId={}, brandId={}",
                applicationDate, productId, brandId);

        var response = getApplicablePriceUseCase.execute(applicationDate, productId, brandId);

        log.info("Price query processed successfully. productId={}, brandId={}, priceList={}",
                response.productId(), response.brandId(), response.priceList());

        return response;
    }
}