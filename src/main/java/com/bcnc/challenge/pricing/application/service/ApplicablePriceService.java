package com.bcnc.challenge.pricing.application.service;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.application.ports.out.LoadApplicablePricePort;
import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ApplicablePriceService implements GetApplicablePriceUseCase {

    private final LoadApplicablePricePort loadApplicablePricePort;

    public ApplicablePriceService(LoadApplicablePricePort loadApplicablePricePort) {
        this.loadApplicablePricePort = loadApplicablePricePort;
    }

    @Override
    public ApplicablePriceResponse execute(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId
    ) {
        validateInput(applicationDate, productId, brandId);

        log.info("Searching applicable price. applicationDate={}, productId={}, brandId={}",
                applicationDate, productId, brandId);

        Price price = loadApplicablePricePort
                .loadApplicablePrice(applicationDate, productId, brandId)
                .orElseThrow(() -> notFound(applicationDate, productId, brandId));

        log.info("Applicable price found. price={}", price);

        return toResponse(price);
    }

    private ApplicablePriceNotFoundException notFound(
            LocalDateTime applicationDate, Long productId, Long brandId) {

        log.warn("No applicable price found. applicationDate={}, productId={}, brandId={}",
                applicationDate, productId, brandId);

        return new ApplicablePriceNotFoundException(
                "No applicable price found for productId=%d, brandId=%d, applicationDate=%s"
                        .formatted(productId, brandId, applicationDate));
    }

    private static ApplicablePriceResponse toResponse(Price price) {
        return new ApplicablePriceResponse(
                price.product().id(),
                price.brand().id(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.amount()
        );
    }

    private static void validateInput(LocalDateTime applicationDate, Long productId, Long brandId) {
        if (applicationDate == null) throw new IllegalArgumentException("applicationDate must not be null");
        if (productId == null)       throw new IllegalArgumentException("productId must not be null");
        if (brandId == null)         throw new IllegalArgumentException("brandId must not be null");
        if (productId <= 0)          throw new IllegalArgumentException("productId must be greater than zero");
        if (brandId <= 0)            throw new IllegalArgumentException("brandId must be greater than zero");
    }
}