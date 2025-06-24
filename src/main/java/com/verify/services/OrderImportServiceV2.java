package com.verify.services;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.verify.dto.OrderRevenueImportDTO;
import com.verify.dto.SalesImport;
import com.verify.entity.*;
import com.verify.repository.KsaCityRepository;
import com.verify.repository.OrderRevenueRepository;
import com.verify.repository.OrderRevenueRepositoryV2;
import com.verify.repository.PricingRuleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderImportServiceV2 {

    private final OrderRevenueRepositoryV2 orderRevenueRepository;



    public void importOrders(MultipartFile file) throws IOException {
        EasyExcel.read(
                file.getInputStream(), SalesImport.class, new AnalysisEventListener<SalesImport>() {

                    private final List<SalesImport> batchList = new ArrayList<>();

                    @Override
                    public void invoke(SalesImport dto, AnalysisContext context) {

                        batchList.add(dto);
                        if (batchList.size() >= 100) {
                            saveBatch(batchList);
                            batchList.clear();
                        }
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        if (!batchList.isEmpty()) {
                            saveBatch(batchList);
                        }
                    }
                }).sheet().doRead();
    }

    private void saveBatch(List<SalesImport> dataList) {
        List<OrderRevenueV2> orderRevenueList = dataList.stream().map(this::mapToEntity)
                .filter(Optional::isPresent)  // 过滤掉 Optional.empty()
                .map(Optional::get).collect(Collectors.toList());
        orderRevenueRepository.saveAll(orderRevenueList);
    }

    private Optional<OrderRevenueV2> mapToEntity(SalesImport dto) {
        OrderRevenueV2 order = new OrderRevenueV2();
        order.setWaybill(dto.getWaybill());
        order.setChargeableWeight(dto.getChargeableWeight());
        order.setPickupDate(dto.getPickupDate());


        order.setFreight1(calculateUAEFreight(dto.getChargeableWeight()));
        order.setFreight2(calculateUAEFreight2(dto.getChargeableWeight()));



        return Optional.of(order);
    }

    public static BigDecimal calculateUAEFreight(BigDecimal weight) {
        BigDecimal firstWeightLimit = BigDecimal.valueOf(0.5); // 首重限制
        BigDecimal firstWeightPrice = BigDecimal.valueOf(5.34); // 首重价格
        BigDecimal additionalWeightUnit = BigDecimal.valueOf(0.5); // 续重单位
        BigDecimal additionalUnitPrice = BigDecimal.valueOf(2.18); // 每单位续重价格

        BigDecimal shippingCost = BigDecimal.ZERO;

        // 如果重量小于等于首重，直接收首重价
        if (weight.compareTo(firstWeightLimit) <= 0) {
            return firstWeightPrice;
        }

        // 首重部分
        shippingCost = shippingCost.add(firstWeightPrice);

        // 续重部分，向上取整
        BigDecimal excessWeight = weight.subtract(firstWeightLimit);
        BigDecimal additionalUnits = excessWeight.divide(additionalWeightUnit, 0, RoundingMode.CEILING);

        shippingCost = shippingCost.add(additionalUnits.multiply(additionalUnitPrice));

        return shippingCost;
    }
    public static BigDecimal calculateUAEFreight2(BigDecimal weight) {
        BigDecimal firstWeightLimit = BigDecimal.valueOf(0.1); // 首重限制
        BigDecimal firstWeightPrice = BigDecimal.valueOf(4.3); // 首重价格
        BigDecimal additionalWeightUnit = BigDecimal.valueOf(0.1); // 续重单位
        BigDecimal additionalUnitPrice = BigDecimal.valueOf(0.448); // 每单位续重价格

        BigDecimal shippingCost = BigDecimal.ZERO;

        // 如果重量小于等于首重，直接收首重价
        if (weight.compareTo(firstWeightLimit) <= 0) {
            return firstWeightPrice;
        }

        // 首重部分
        shippingCost = shippingCost.add(firstWeightPrice);

        // 续重部分，向上取整
        BigDecimal excessWeight = weight.subtract(firstWeightLimit);
        BigDecimal additionalUnits = excessWeight.divide(additionalWeightUnit, 0, RoundingMode.CEILING);

        shippingCost = shippingCost.add(additionalUnits.multiply(additionalUnitPrice));

        return shippingCost;
    }
}
