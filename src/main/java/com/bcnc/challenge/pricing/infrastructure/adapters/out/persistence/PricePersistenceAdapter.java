package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence;

import com.bcnc.challenge.pricing.application.ports.out.LoadApplicablePricePort;
import com.bcnc.challenge.pricing.domain.model.AuditMetadata;
import com.bcnc.challenge.pricing.domain.model.Brand;
import com.bcnc.challenge.pricing.domain.model.Currency;
import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.domain.model.Product;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.PriceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PricePersistenceAdapter implements LoadApplicablePricePort {

    private static final Logger log = LoggerFactory.getLogger(PricePersistenceAdapter.class);

    private final PriceJpaRepository priceJpaRepository;

    public PricePersistenceAdapter(PriceJpaRepository priceJpaRepository) {
        this.priceJpaRepository = priceJpaRepository;
    }

    @Override
    public Optional<Price> loadApplicablePrice(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId
    ) {
        log.info(
                "Loading applicable price from persistence. applicationDate={}, productId={}, brandId={}",
                applicationDate,
                productId,
                brandId
        );

        Optional<Price> result = priceJpaRepository
                .findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
                        productId,
                        brandId,
                        applicationDate,
                        applicationDate
                )
                .map(this::toDomain);

        log.info(
                "Persistence lookup finished. found={}",
                result.isPresent()
        );

        return result;
    }

    private Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getId(),
                new Brand(entity.getBrand().getId(), entity.getBrand().getName()),
                new Product(entity.getProduct().getId(), entity.getProduct().getName()),
                entity.getPriceList(),
                entity.getPriority(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getAmount(),
                new Currency(entity.getCurrency().getIsoCode(), entity.getCurrency().getDescription()),
                new AuditMetadata(
                        entity.isActive(),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                )
        );
    }
}