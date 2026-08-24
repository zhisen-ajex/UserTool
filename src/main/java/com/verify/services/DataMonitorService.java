package com.verify.services;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.verify.dto.OrderRevenueImportDTO;
import com.verify.enums.ReportSql;
import com.verify.repository.DataMonitorRepository;
import com.verify.repository.OrderRevenueExportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataMonitorService {

    private static final int BATCH_SIZE = 2000;
    private static final int IMPORT_BATCH_SIZE = 100;
    private static final String ORDER_REVENUE_SHEET_NAME = "Order Revenue";

    private final SqlFileLoader sqlFileLoader;
    private final DataMonitorRepository repository;
    private final OrderRevenueExportRepository orderRevenueExportRepository;
    private final OrderImportService orderImportService;

    public void export(OutputStream out) {
        ExcelWriter writer = null;
        try {
            writer = EasyExcel.write(out).build();
            ReportSql[] reports = ReportSql.values();
            for (int sheetNo = 0; sheetNo < reports.length; sheetNo++) {
                ReportSql report = reports[sheetNo];
                log.info("Start exporting sheet: {}", report.getSheetName());
                processSheet(writer, report, sheetNo);
                log.info("Finished exporting sheet: {}", report.getSheetName());
            }

            // Freight 数据此时已全部落库到 order_revenue 表，追加生成 "Order Revenue" 报表 sheet
            LocalDate reportDate = LocalDate.now(ZoneId.of("Asia/Shanghai")).minusDays(1);
            processOrderRevenueSheet(writer, reportDate, reports.length);
            log.info("Finished exporting sheet: {}", ORDER_REVENUE_SHEET_NAME);
        } catch (Exception e) {
            log.error("Export task failed", e);
            throw new RuntimeException("Export failed", e);
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

    private void processOrderRevenueSheet(ExcelWriter writer, LocalDate reportDate, int sheetNo) {
        String sql = buildOrderRevenueSql(reportDate);
        List<List<Object>> batch = new ArrayList<>(BATCH_SIZE);
        WriteSheet[] sheetHolder = new WriteSheet[1];
        int[] rowCount = new int[1];

        orderRevenueExportRepository.queryStream(sql, (columnNames, row) -> {
            if (sheetHolder[0] == null) {
                sheetHolder[0] = EasyExcel.writerSheet(sheetNo, ORDER_REVENUE_SHEET_NAME)
                        .head(buildHeadFromColumns(columnNames))
                        .build();
            }

            batch.add(row);
            rowCount[0]++;
            if (batch.size() >= BATCH_SIZE) {
                flushBatch(writer, sheetHolder[0], batch);
            }
        });

        if (sheetHolder[0] == null) {
            sheetHolder[0] = EasyExcel.writerSheet(sheetNo, ORDER_REVENUE_SHEET_NAME).build();
            writer.write(Collections.<List<Object>>emptyList(), sheetHolder[0]);
            log.info("Order revenue sheet exported with no data rows for {}.", reportDate);
            return;
        }

        flushBatch(writer, sheetHolder[0], batch);
        log.info("Order revenue sheet exported rows: {} for {}.", rowCount[0], reportDate);
    }

    private String buildOrderRevenueSql(LocalDate reportDate) {
        String tableName = "order_revenue_" + reportDate.getMonthValue() + "_" + reportDate.getDayOfMonth();
        return "SELECT " +
                "DATE_FORMAT(FROM_UNIXTIME(CAST(pickup_date AS SIGNED) / 1000 + 8 * 3600), '%c月%e日') AS `Date`, " +
                "customer_code AS `Customer Code`, " +
                "customer_name AS `Customer Name`, " +
                "COUNT(order_id) AS `Daily Pickuped Orders`, " +
                "SUM(freight) AS `Total Freight Revenue(SAR)`, " +
                "SUM(chargeable_weight) AS `Total Chargeable Weight(KG)` " +
                "FROM `" + tableName + "` " +
                "GROUP BY DATE_FORMAT(FROM_UNIXTIME(CAST(pickup_date AS SIGNED) / 1000 + 8 * 3600), '%c月%e日'), " +
                "customer_code, customer_name " +
                "ORDER BY STR_TO_DATE(`Date`, '%c月%e日') DESC, SUM(freight) DESC";
    }

    private void processSheet(ExcelWriter writer, ReportSql report, int sheetNo) {
        String sql = sqlFileLoader.load(report.getFileName());
        List<List<Object>> batch = new ArrayList<>(BATCH_SIZE);
        List<OrderRevenueImportDTO> importBatch =
                report == ReportSql.FREIGHT ? new ArrayList<>(IMPORT_BATCH_SIZE) : null;
        WriteSheet[] sheetHolder = new WriteSheet[1];
        int[] rowCount = new int[1];

        repository.queryStream(sql, (columnNames, row) -> {
            if (sheetHolder[0] == null) {
                sheetHolder[0] = EasyExcel.writerSheet(sheetNo, report.getSheetName())
                        .head(buildHeadFromColumns(columnNames))
                        .build();
            }

            batch.add(row);
            rowCount[0]++;

            // Freight 报表读取的同时，复用 OrderImportService 的落库逻辑保存到 order_revenue 表
            if (importBatch != null) {
                importBatch.add(mapToOrderRevenueImportDTO(columnNames, row));
                if (importBatch.size() >= IMPORT_BATCH_SIZE) {
                    orderImportService.importOrderData(importBatch);
                    importBatch.clear();
                }
            }

            if (batch.size() >= BATCH_SIZE) {
                flushBatch(writer, sheetHolder[0], batch);
            }
        });

        if (importBatch != null && !importBatch.isEmpty()) {
            orderImportService.importOrderData(importBatch);
        }

        if (sheetHolder[0] == null) {
            sheetHolder[0] = EasyExcel.writerSheet(sheetNo, report.getSheetName()).build();
            writer.write(Collections.<List<Object>>emptyList(), sheetHolder[0]);
            log.info("Sheet {} exported with no data rows.", report.getSheetName());
            return;
        }

        flushBatch(writer, sheetHolder[0], batch);
        log.info("Sheet {} exported rows: {}", report.getSheetName(), rowCount[0]);
    }

    /**
     * 将 Freight 报表查询结果的一行数据映射为 OrderRevenueImportDTO，
     * 列名与 OrderRevenueImportDTO 的 @ExcelProperty 一一对应。
     */
    private OrderRevenueImportDTO mapToOrderRevenueImportDTO(List<String> columns, List<Object> row) {
        OrderRevenueImportDTO dto = new OrderRevenueImportDTO();
        for (int i = 0; i < columns.size() && i < row.size(); i++) {
            String column = columns.get(i);
            Object value = row.get(i);
            if (value == null) {
                continue;
            }
            switch (column) {
                case "Pickup Date":
                    dto.setPickupDate(asString(value));
                    break;
                case "Reference Number":
                    dto.setOrderId(asString(value));
                    break;
                case "TrackingId":
                    dto.setWaybill(asString(value));
                    break;
                case "Account Number":
                    dto.setCustomerCode(asString(value));
                    break;
                case "Account Name":
                    dto.setCustomerName(asString(value));
                    break;
                case "product_code":
                    dto.setProductCode(asString(value));
                    break;
                case "origin_country":
                    dto.setSenderCountry(asString(value));
                    break;
                case "origin_city":
                    dto.setSenderCity(asString(value));
                    break;
                case "destination_country":
                    dto.setConsigneeCountry(asString(value));
                    break;
                case "destination_city":
                    dto.setConsigneeCity(asString(value));
                    break;
                case "destination_district":
                    dto.setConsigneeState(asString(value));
                    break;
                case "chargeable_weight":
                    dto.setChargeableWeight(asBigDecimal(value));
                    break;
                case "weight_unit":
                    dto.setWeightUnit(asString(value));
                    break;
                case "cod_amount":
                    dto.setCodAmount(asBigDecimal(value));
                    break;
                case "cod_currency":
                    dto.setCodCurrency(asString(value));
                    break;
                case "declared_value":
                    dto.setDeclaredValue(asBigDecimal(value));
                    break;
                case "declared_value_currency":
                    dto.setDeclaredValueCurrency(asString(value));
                    break;
                case "status":
                    dto.setStatus(asString(value));
                    break;
                default:
                    break;
            }
        }
        return dto;
    }

    private String asString(Object value) {
        return value.toString();
    }

    private BigDecimal asBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }

    private void flushBatch(ExcelWriter writer, WriteSheet sheet, List<List<Object>> batch) {
        if (batch.isEmpty()) {
            return;
        }

        writer.write(batch, sheet);
        batch.clear();
    }

    private List<List<String>> buildHeadFromColumns(List<String> columns) {
        List<List<String>> head = new ArrayList<>();
        if (columns == null || columns.isEmpty()) {
            return head;
        }
        for (String col : columns) {
            head.add(Collections.singletonList(col));
        }
        return head;
    }
}
