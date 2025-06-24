package com.verify.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerCode;
    private String customerName;
    private String products;
    private String country;
    private BigDecimal baseWeight;
    private BigDecimal basePrice;
    private BigDecimal extraPricePerKg;
    private BigDecimal extraPricePer0_1kg;
    private BigDecimal extraPricePer0_5kg;

    private String type;
    // COD 计费规则
    private String codFeeType; // "PERCENTAGE" or "FIXED"
    private BigDecimal codFeeValue;


    private String currency;
}
