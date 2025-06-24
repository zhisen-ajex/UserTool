package com.verify.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRevenueTemuImportDTO {
    @ExcelProperty("Reference Number")
    private String waybill;

    @ExcelProperty("Customer Reference Number")
    private String orderId;

    @ExcelProperty("Customer Name")
    private String customerName;

    @ExcelProperty("Customer Code")
    private String customerCode;

    @ExcelProperty("Status")
    private String status;

    @ExcelProperty("Sender Country")
    private String senderCountry;

//    @ExcelProperty("origin_city")
//    private String senderCity;

    @ExcelProperty("Consignee Country")
    private String consigneeCountry;

    @ExcelProperty("Consignee City")
    private String consigneeCity;

    @ExcelProperty("Consignee State")
    private String consigneeState;

    @ExcelProperty("Chargeable Weight")
    private BigDecimal chargeableWeight;

    @ExcelProperty("COD Amount")
    private BigDecimal codAmount;

    @ExcelProperty("cod_currency")
    private String codCurrency;

    @ExcelProperty("Declared Value")
    private BigDecimal declaredValue;

    @ExcelProperty("Declared Currency")
    private String declaredValueCurrency;

    @ExcelProperty("Service Type Id")
    private String productCode;


    @ExcelProperty("Delivery Date")
    private String deliveryDate;

    @ExcelProperty("Pickup Date")
    private String pickupDate;
}
