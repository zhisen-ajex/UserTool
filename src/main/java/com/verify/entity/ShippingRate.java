package com.verify.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_rate",
        uniqueConstraints = {@UniqueConstraint(name = "uniq_rule", columnNames = {"country_code", "service_type", "weight_min"})},
        indexes = {@Index(name = "idx_weight_range", columnList = "weight_min, weight_max")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "weight_min", nullable = false, precision = 10, scale = 3)
    private BigDecimal weightMin;

    @Column(name = "weight_max", nullable = false, precision = 10, scale = 3)
    private BigDecimal weightMax;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "extra_per_kg", precision = 10, scale = 2)
    private BigDecimal extraPerKg = BigDecimal.ZERO;

    @Column(name = "cod_rate", precision = 5, scale = 2)
    private BigDecimal codRate = BigDecimal.ZERO;

    @Column(name = "min_cod", precision = 10, scale = 2)
    private BigDecimal minCod = BigDecimal.valueOf(1.10);

    @Column(name = "clearance_per_kg", nullable = false, precision = 10, scale = 3)
    private BigDecimal clearancePerKg;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
}
