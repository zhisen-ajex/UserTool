package com.verify.services;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.verify.enums.ReportSql;
import com.verify.repository.DataMonitorRepository;
import com.verify.repository.OrderRevenueExportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataMonitorService {

    private static final int BATCH_SIZE = 2000;
    private static final String ORDER_REVENUE_SHEET_NAME = "Order Revenue";

    private final SqlFileLoader sqlFileLoader;
    private final DataMonitorRepository repository;
    private final OrderRevenueExportRepository orderRevenueExportRepository;

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
        } catch (Exception e) {
            log.error("Export task failed", e);
            throw new RuntimeException("Export failed", e);
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

    public void exportOrderRevenue(OutputStream out, LocalDate reportDate) {
        ExcelWriter writer = null;
        try {
            writer = EasyExcel.write(out).build();
            processOrderRevenueSheet(writer, reportDate);
        } catch (Exception e) {
            log.error("Order revenue export task failed for {}", reportDate, e);
            throw new RuntimeException("Order revenue export failed", e);
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

    private void processOrderRevenueSheet(ExcelWriter writer, LocalDate reportDate) {
        String sql = buildOrderRevenueSql(reportDate);
        List<List<Object>> batch = new ArrayList<>(BATCH_SIZE);
        WriteSheet[] sheetHolder = new WriteSheet[1];
        int[] rowCount = new int[1];

        orderRevenueExportRepository.queryStream(sql, (columnNames, row) -> {
            if (sheetHolder[0] == null) {
                sheetHolder[0] = EasyExcel.writerSheet(0, ORDER_REVENUE_SHEET_NAME)
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
            sheetHolder[0] = EasyExcel.writerSheet(0, ORDER_REVENUE_SHEET_NAME).build();
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

            if (batch.size() >= BATCH_SIZE) {
                flushBatch(writer, sheetHolder[0], batch);
            }
        });

        if (sheetHolder[0] == null) {
            sheetHolder[0] = EasyExcel.writerSheet(sheetNo, report.getSheetName()).build();
            writer.write(Collections.<List<Object>>emptyList(), sheetHolder[0]);
            log.info("Sheet {} exported with no data rows.", report.getSheetName());
            return;
        }

        flushBatch(writer, sheetHolder[0], batch);
        log.info("Sheet {} exported rows: {}", report.getSheetName(), rowCount[0]);
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
