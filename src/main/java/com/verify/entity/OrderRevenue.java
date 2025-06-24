package com.verify.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_revenue_z")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String waybill;
    private String orderId;
    private String customerCode;
    private String customerName;
    private String status;
    private String deliveryEventTime;
    private BigDecimal chargeableWeight;
    private BigDecimal codAmount;
    private String codCurrency;
    private String productCode;
    private BigDecimal freight;
    private BigDecimal codFee;



    private String senderCountry;
    private String senderCity;

    private String consigneeCountry;

    private String consigneeCity;

    private String consigneeState;
    private String consigneeTier;

    private BigDecimal declaredValue;

    private String declaredValueCurrency;


    private String deliveryDate;

    private String pickupDate;

    private String type;



}
