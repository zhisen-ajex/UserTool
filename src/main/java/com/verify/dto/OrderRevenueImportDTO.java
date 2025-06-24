package com.verify.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRevenueImportDTO {
    @ExcelProperty("TrackingId")
    private String waybill;

    @ExcelProperty("Reference Number")
    private String orderId;

    @ExcelProperty("Account Name")
    private String customerName;

    @ExcelProperty("Account Number")
    private String customerCode;

    @ExcelProperty("status")
    private String status;

    @ExcelProperty("origin_country")
    private String senderCountry;

    @ExcelProperty("origin_city")
    private String senderCity;

    @ExcelProperty("destination_country")
    private String consigneeCountry;

    @ExcelProperty("destination_city")
    private String consigneeCity;

    @ExcelProperty("destination_district")
    private String consigneeState;

    @ExcelProperty("chargeable_weight")
    private BigDecimal chargeableWeight;

    @ExcelProperty("cod_amount")
    private BigDecimal codAmount;

    @ExcelProperty("cod_currency")
    private String codCurrency;

    @ExcelProperty("declared_value")
    private BigDecimal declaredValue;

    @ExcelProperty("declared_value_currency")
    private String declaredValueCurrency;

    @ExcelProperty("product_code")
    private String productCode;


    @ExcelProperty("Delivery Date")
    private String deliveryDate;

    @ExcelProperty("Pickup Date")
    private String pickupDate;
}
