package com.verify.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Jeffery Xie
 * @since 2025/07/11
 */
@Data
public class RemoteCityImportDTO {


    @ExcelProperty("cityName")
    private String cityName;

    @ExcelProperty("cityCode")
    private String cityCode;

    @ExcelProperty("countryIsoCode3")
    private String countryIsoCode3;

    @ExcelProperty("region_code")
    private String regionCode;

    @ExcelProperty("remote")
    private Boolean isRemote;

}
