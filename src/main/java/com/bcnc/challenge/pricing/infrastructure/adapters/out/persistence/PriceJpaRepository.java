package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence;

import com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    @Query(value = """
            SELECT p.*
            FROM prices p
            WHERE p.product_id = :productId
              AND p.brand_id = :brandId
              AND p.active = true
              AND p.start_date <= :applicationDate
              AND p.end_date >= :applicationDate
            ORDER BY p.priority DESC, p.start_date DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<PriceEntity> findApplicablePrice(
            @Param("productId") Long productId,
            @Param("brandId") Long brandId,
            @Param("applicationDate") LocalDateTime applicationDate
    );
}