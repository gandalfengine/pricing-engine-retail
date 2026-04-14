package com.bcnc.challenge.pricing.application.service;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.application.ports.out.LoadApplicablePricePort;
import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.application.result.ApplicablePriceResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ApplicablePriceService implements GetApplicablePriceUseCase {

    private final LoadApplicablePricePort loadApplicablePricePort;
    private final Timer applicablePriceExecutionTimer;
    private final Counter applicablePriceRequestCounter;
    private final Counter applicablePriceFoundCounter;
    private final Counter applicablePriceNotFoundCounter;
    private final Counter applicablePriceValidationErrorCounter;

    public ApplicablePriceService(LoadApplicablePricePort loadApplicablePricePort,
                                  MeterRegistry meterRegistry) {
        this.loadApplicablePricePort = loadApplicablePricePort;
        this.applicablePriceExecutionTimer = Timer.builder("pricing.applicable_price.execution")
                .description("Time taken to resolve applicable price")
                .register(meterRegistry);
        this.applicablePriceRequestCounter = Counter.builder("pricing.applicable_price.requests")
                .description("Total applicable price requests")
                .register(meterRegistry);
        this.applicablePriceFoundCounter = Counter.builder("pricing.applicable_price.found")
                .description("Total applicable price requests with result")
                .register(meterRegistry);
        this.applicablePriceNotFoundCounter = Counter.builder("pricing.applicable_price.not_found")
                .description("Total applicable price requests without result")
                .register(meterRegistry);
        this.applicablePriceValidationErrorCounter = Counter.builder("pricing.applicable_price.validation_error")
                .description("Total applicable price requests rejected by input validation")
                .register(meterRegistry);
    }

    @Override
    public ApplicablePriceResult execute(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId
    ) {
        applicablePriceRequestCounter.increment();
        Timer.Sample executionTimerSample = Timer.start();

        try {
            validateInput(applicationDate, productId, brandId);

            log.info("Searching applicable price. applicationDate={}, productId={}, brandId={}",
                    applicationDate, productId, brandId);

            Price price = loadApplicablePricePort
                    .loadApplicablePrice(applicationDate, productId, brandId)
                    .orElseThrow(() -> notFound(applicationDate, productId, brandId));

            log.info("Applicable price found. price={}", price);

            applicablePriceFoundCounter.increment();
            return toResponse(price);
        } catch (IllegalArgumentException ex) {
            applicablePriceValidationErrorCounter.increment();
            throw ex;
        } finally {
            executionTimerSample.stop(applicablePriceExecutionTimer);
        }
    }

    private ApplicablePriceNotFoundException notFound(
            LocalDateTime applicationDate, Long productId, Long brandId) {

        applicablePriceNotFoundCounter.increment();

        log.warn("No applicable price found. applicationDate={}, productId={}, brandId={}",
                applicationDate, productId, brandId);

        return new ApplicablePriceNotFoundException(
                "No applicable price found for productId=%d, brandId=%d, applicationDate=%s"
                        .formatted(productId, brandId, applicationDate));
    }

    private static ApplicablePriceResult toResponse(Price price) {
        return new ApplicablePriceResult(
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