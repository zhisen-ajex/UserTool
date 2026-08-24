package com.verify.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ReportSql {

    NON_TEMU_SHEIN("NonTemu_shein", "NonTemu_shein.sql"),

    SHEIN_TEMU("Shein_Temu", "Shein_Temu.sql"),

    ALL("All", "All.sql"),

    FREIGHT("Freight", "Freight.sql");


    private final String sheetName;

    private final String fileName;

}