package com.verify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_revenue_v2_2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenueV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String waybill;

    private String pickupDate;
    private BigDecimal chargeableWeight;
    private BigDecimal freight1;
    private BigDecimal freight2;



}
