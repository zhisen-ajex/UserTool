package com.verify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.verify.services.OrderImportService;
import com.verify.services.OrderImportServiceV2;
import com.verify.services.ShipperInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/excel")
@AllArgsConstructor
public class OrderRevenueImportController {

    @Value("#{${order.detail.account.no}}")
    private Map<String,String> accountNo;
    private final OrderImportService orderImportService;
    private final ShipperInfoService shipperInfoService;
    private final OrderImportServiceV2 orderImportServiceV2;

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

    @PostMapping("/import33")
    public void importRemoteCity(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }
        try {

            orderImportService.importRemote(file);
        } catch (IOException e) {
            log.error("Excel 解析失败", e);

        }
    }



    @PostMapping
    public Map<String,String> shipperInfoService() throws JsonProcessingException {

        log.info("==============================Result==============================");
//        log.info(shipperInfoService.getFormattedShipperNamesForProperties());
        return accountNo;
    }

    @PostMapping("/import2")
    public void importExcel2(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }
        try {

            orderImportServiceV2.importOrders(file);
        } catch (IOException e) {
            log.error("Excel 解析失败", e);

        }
    }
}
