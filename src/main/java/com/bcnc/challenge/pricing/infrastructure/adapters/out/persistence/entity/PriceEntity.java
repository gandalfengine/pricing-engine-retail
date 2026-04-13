package com.bcnc.challenge.pricing.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "prices",
        indexes = {
                @Index(name = "idx_prices_brand_product_dates", columnList = "brand_id, product_id, start_date, end_date"),
                @Index(name = "idx_prices_priority", columnList = "priority")
        }
)
public class PriceEntity extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brand;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "price_list", nullable = false)
    private Integer priceList;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code", nullable = false)
    private CurrencyEntity currency;

    protected PriceEntity() {
    }

    public PriceEntity(
            Long id,
            BrandEntity brand,
            ProductEntity product,
            Integer priceList,
            Integer priority,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal amount,
            CurrencyEntity currency
    ) {
        this.id = id;
        this.brand = brand;
        this.product = product;
        this.priceList = priceList;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.currency = currency;
    }

}