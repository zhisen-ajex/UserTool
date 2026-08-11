package com.verify.services;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.verify.enums.ReportSql;
import com.verify.repository.DataMonitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataMonitorService {

    private static final int BATCH_SIZE = 2000;

    private final SqlFileLoader sqlFileLoader;
    private final DataMonitorRepository repository;

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
