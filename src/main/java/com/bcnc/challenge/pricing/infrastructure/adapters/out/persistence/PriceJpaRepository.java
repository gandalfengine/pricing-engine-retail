package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence;

import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    Optional<PriceEntity> findTopByProduct_IdAndBrand_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrueOrderByPriorityDesc(
            Long productId,
            Long brandId,
            LocalDateTime applicationDate1,
            LocalDateTime applicationDate2
    );
}