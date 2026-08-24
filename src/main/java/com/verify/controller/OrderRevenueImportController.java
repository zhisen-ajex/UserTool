package com.verify.controller;

import com.verify.services.DataMonitorService;
import com.verify.services.OrderImportService;
import com.verify.services.OrderImportServiceV2;
import com.verify.services.ShipperInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/excel")
@AllArgsConstructor
public class OrderRevenueImportController {

    @Value("#{${order.detail.account.no}}")
    private Map<String, String> accountNo;
    private final OrderImportService orderImportService;
    private final ShipperInfoService shipperInfoService;
    private final OrderImportServiceV2 orderImportServiceV2;
    private final DataMonitorService dataMonitorService;

    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }
        try {

            orderImportService.importOrders(file);
        } catch (IOException e) {
            log.error("Excel 解析失败", e);

        }
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        String fileName = "ajex_report_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".xlsx";

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        response.reset();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        dataMonitorService.export(response.getOutputStream());
        response.flushBuffer();
    }


//    @PostMapping("/import33")
//    public void importRemoteCity(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return;
//        }
//        try {
//
//            orderImportService.importRemote(file);
//        } catch (IOException e) {
//            log.error("Excel 解析失败", e);
//
//        }
//    }
//
//
//
//    @PostMapping
//    public Map<String,String> shipperInfoService() throws JsonProcessingException {
//
//        log.info("==============================Result==============================");
////        log.info(shipperInfoService.getFormattedShipperNamesForProperties());
//        return accountNo;
//    }
//
//    @PostMapping("/import2")
//    public void importExcel2(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return;
//        }
//        try {
//
//            orderImportServiceV2.importOrders(file);
//        } catch (IOException e) {
//            log.error("Excel 解析失败", e);
//
//        }
//    }
}
