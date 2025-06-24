package com.verify.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesImport {
    @ExcelProperty("TrackingId")
    private String waybill;


    @ExcelProperty("chargeable_weight")
    private BigDecimal chargeableWeight;


    @ExcelProperty("Pickup Date")
    private String pickupDate;
}
