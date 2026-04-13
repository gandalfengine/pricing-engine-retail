package com.bcnc.challenge.pricing.application.service;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.application.ports.out.LoadApplicablePricePort;
import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApplicablePriceService implements GetApplicablePriceUseCase {

    private static final Logger log = LoggerFactory.getLogger(ApplicablePriceService.class);

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
        log.info(
                "Searching applicable price. applicationDate={}, productId={}, brandId={}",
                applicationDate,
                productId,
                brandId
        );

        Price price = loadApplicablePricePort
                .loadApplicablePrice(applicationDate, productId, brandId)
                .orElseThrow(() -> {
                    log.warn(
                            "No applicable price found. applicationDate={}, productId={}, brandId={}",
                            applicationDate,
                            productId,
                            brandId
                    );
                    return new ApplicablePriceNotFoundException(
                            "No applicable price found for productId=%d, brandId=%d, applicationDate=%s"
                                    .formatted(productId, brandId, applicationDate)
                    );
                });

        log.info("Applicable price found. price={}", price);

        return new ApplicablePriceResponse(
                price.product().id(),
                price.brand().id(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.amount()
        );
    }
}