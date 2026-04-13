package com.bcnc.challenge.pricing.infrastructure.adapters.in.web;

import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApiResponse;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.bcnc.challenge.pricing.infrastructure.adapters.in.web.filter.CorrelationIdFilter.CORRELATION_ID_KEY;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/prices")
public class PriceQueryController {

    private static final String SUCCESS_MESSAGE = "Applicable price retrieved successfully";

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;

    public PriceQueryController(GetApplicablePriceUseCase getApplicablePriceUseCase) {
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
    }

    @GetMapping
    public ApiResponse<ApplicablePriceResponse> getApplicablePrice(
            @Validated @ModelAttribute ApplicablePriceCriteria criteria,
            @RequestAttribute(CORRELATION_ID_KEY) String correlationId
    ) {
        log.info("Price query received. criteria={}", criteria);

        var response = getApplicablePriceUseCase.execute(
                criteria.applicationDate(),
                criteria.productId(),
                criteria.brandId()
        );

        log.info(
                "Price query processed successfully. productId={}, brandId={}, priceList={}",
                response.productId(),
                response.brandId(),
                response.priceList()
        );

        return ApiResponse.success(
                SUCCESS_MESSAGE,
                correlationId,
                response
        );
    }

    public record ApplicablePriceCriteria(
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @NotNull @Positive Long productId,
            @NotNull @Positive Long brandId
    ) {
    }
}