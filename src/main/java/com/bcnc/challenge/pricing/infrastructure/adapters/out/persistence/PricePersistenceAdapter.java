package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence;

import com.bcnc.challenge.pricing.application.ports.out.LoadApplicablePricePort;
import com.bcnc.challenge.pricing.domain.model.AuditMetadata;
import com.bcnc.challenge.pricing.domain.model.Brand;
import com.bcnc.challenge.pricing.domain.model.Currency;
import com.bcnc.challenge.pricing.domain.model.Price;
import com.bcnc.challenge.pricing.domain.model.Product;
import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.PriceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.bcnc.challenge.pricing.infrastructure.config.CacheConfig.APPLICABLE_PRICE_CACHE;

@Slf4j
@Component
public class PricePersistenceAdapter implements LoadApplicablePricePort {

    private final PriceJpaRepository priceJpaRepository;

    public PricePersistenceAdapter(PriceJpaRepository priceJpaRepository) {
        this.priceJpaRepository = priceJpaRepository;
    }

    @Override
    @Cacheable(
            cacheNames = APPLICABLE_PRICE_CACHE,
            key = "T(com.bcnc.challenge.pricing.infrastructure.config.CacheKeyFactory).applicablePriceKey(#applicationDate, #productId, #brandId)",
            unless = "#result == null"
    )
    public Optional<Price> loadApplicablePrice(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId
    ) {
        log.info(
                "Cache miss. Loading applicable price from persistence. applicationDate={}, productId={}, brandId={}",
                applicationDate,
                productId,
                brandId
        );

        return priceJpaRepository
                .findApplicablePrice(
                        productId,
                        brandId,
                        applicationDate
                )
                .map(this::toDomain);
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