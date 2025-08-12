package com.verify.services;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.verify.dto.OrderRevenueImportDTO;
import com.verify.entity.KsaCity;
import com.verify.entity.OrderRevenue;
import com.verify.entity.PricingRule;
import com.verify.entity.ServiceType;
import com.verify.repository.KsaCityRepository;
import com.verify.repository.OrderRevenueRepository;
import com.verify.repository.PricingRuleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderImportService {

    private final OrderRevenueRepository orderRevenueRepository;
    private final FreightCalculationService freightCalculationService;

    private final PricingRuleRepository pricingRuleRepository;
    private final KsaCityRepository ksaCityRepository;

    private Map<String, List<PricingRule>> pricingRulesCache = new HashMap<>();
    private static final Map<String, BigDecimal> EXCHANGE_RATES = Map.of(
            "AED", new BigDecimal("1.02"),  // 阿联酋迪拉姆 -> SAR
            "SAR", BigDecimal.ONE,           // 沙特里亚尔 -> SAR (不变)
            "KWD", new BigDecimal("12.20"),  // 科威特第纳尔 -> SAR
            "BHD", new BigDecimal("9.95"),   // 巴林第纳尔 -> SAR
            "CNY", new BigDecimal("0.52"),   // 人民币 -> SAR
            "USD", new BigDecimal("3.75")    // 美元 -> SAR
    );

    private static final Map<String, String> COUNTRY_CURRENCY_MAP = Map.of(
            "SAUDI ARABIA", "SAR",
            "SAU", "SAR",
            "UNITED ARAB EMIRATES", "AED",
            "CHN", "CNY",
            "CHINA", "CNY",
            "BAHRAIN", "BHD",
            "KUWAIT", "KWD"
    );
    @PostConstruct
    public void loadPricingRules() {
        log.info("Loading all pricing rules into memory...");
        List<PricingRule> allPricingRules = pricingRuleRepository.findAll();
        pricingRulesCache = allPricingRules.stream().collect(Collectors.groupingBy(PricingRule::getCustomerCode));
        log.info("Loaded {} pricing rules", allPricingRules.size());
    }

    public void importOrders(MultipartFile file) throws IOException {
        EasyExcel.read(
                file.getInputStream(), OrderRevenueImportDTO.class, new AnalysisEventListener<OrderRevenueImportDTO>() {

                    private final List<OrderRevenueImportDTO> batchList = new ArrayList<>();

                    @Override
                    public void invoke(OrderRevenueImportDTO dto, AnalysisContext context) {

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

    private void saveBatch(List<OrderRevenueImportDTO> dataList) {
        List<OrderRevenue> orderRevenueList = dataList.stream().map(this::mapToEntity)
                .filter(Optional::isPresent)  // 过滤掉 Optional.empty()
                .map(Optional::get).collect(Collectors.toList());
        orderRevenueRepository.saveAll(orderRevenueList);
    }

    private Optional<OrderRevenue> mapToEntity(OrderRevenueImportDTO dto) {
        OrderRevenue order = new OrderRevenue();
        order.setWaybill(dto.getWaybill());
        order.setOrderId(dto.getOrderId());
        order.setCustomerCode(dto.getCustomerCode());
        order.setCustomerName(dto.getCustomerName());
        order.setStatus(dto.getStatus());
        order.setChargeableWeight(dto.getChargeableWeight());
        order.setCodAmount(dto.getCodAmount());
        order.setCodCurrency(dto.getCodCurrency());
        order.setSenderCountry(dto.getSenderCountry());
        order.setConsigneeCountry(dto.getConsigneeCountry());
        order.setConsigneeCity(dto.getConsigneeCity());
        order.setConsigneeState(dto.getConsigneeState());
        order.setProductCode(dto.getProductCode());
        order.setDeclaredValue(dto.getDeclaredValue());
        order.setDeclaredValueCurrency(dto.getDeclaredValueCurrency());
        order.setPickupDate(dto.getPickupDate());
        order.setDeliveryDate(dto.getDeliveryDate());
        order.setStatus(dto.getStatus());
        if(dto.getCodAmount()==null){
            dto.setCodAmount(BigDecimal.ZERO);
        }
        String productCode = dto.getProductCode().replace("AJEX ", "");
        String type = dto.getCodAmount().compareTo(BigDecimal.ZERO) > 0 ? "COD" : "PPD";

        order.setType(type);
        if ("AJEX850".equals(dto.getCustomerCode()) && ("SAUDI ARABIA".equals(dto.getConsigneeCountry())||"SAU".equals(dto.getConsigneeCountry()))) {
            String country= null;
            KsaCity ksaCity= ksaCityRepository.findByCode(dto.getConsigneeCity());
            if(ksaCity==null){
                log.error("ksaCity ERROR:{} ={} is null",dto.getOrderId(),dto.getConsigneeCity());
                return Optional.of(order);
            }
            if("SAU-TIER1".equals(ksaCity.getTierCode())){
                country="KSA-T1";
            }else  if("SAU-TIER2".equals(ksaCity.getTierCode())){
                country="KSA-T2";
            }else  if("SAU-TIER3".equals(ksaCity.getTierCode())){
                country="KSA-T3";
            }
            BigDecimal freight = freightCalculationService.calculateFreight(country,dto.getCodAmount().compareTo(BigDecimal.ZERO) > 0 ? ServiceType.COD:ServiceType.PPD, dto.getChargeableWeight());
            BigDecimal exchangeRate = EXCHANGE_RATES.getOrDefault( "USD", BigDecimal.ONE);
            freight = freight.multiply(exchangeRate);
            order.setFreight(freight);
            order.setConsigneeTier(country);
            return Optional.of(order);
        }

        if ("AJEX850".equals(dto.getCustomerCode()) && "ARE".equals(dto.getConsigneeCountry())) {
            order.setFreight(freightCalculationService.calculateUAEFreight(dto.getChargeableWeight(), dto.getCodAmount().compareTo(BigDecimal.ZERO) > 0 ? ServiceType.COD : ServiceType.PPD));
            return Optional.of(order);
        }
//        List<PricingRule> ruleOpt = null;
        List<PricingRule> pricingRules = pricingRulesCache.getOrDefault(dto.getCustomerCode(), Collections.emptyList());

        // 1️⃣ **先尝试匹配具体产品的定价规则**
        Optional<PricingRule> ruleOpt = pricingRules.stream()
                .filter(rule -> rule.getProducts().contains(productCode)) // 匹配产品
                .filter(rule -> "AJEX850".equals(dto.getCustomerCode()) ?
                        rule.getCountry().equals(dto.getConsigneeCountry()) &&
                                rule.getType().equals(type)
                        : true) // 仅对 "AJEX850" 额外筛选
                .findFirst(); // 获取第一个匹配的规则

        // 2️⃣ **如果未找到 & 产品是 "AJEX CCX" 或 "CCX"，则返回该客户的任何一个规则**
        if (ruleOpt.isEmpty() && ("AJEX CCX".equals(dto.getProductCode()) || "CCX".equals(dto.getProductCode()))) {
            ruleOpt = pricingRules.stream().findFirst();
        }

        // 3️⃣ **如果仍未找到，记录错误日志**
        if (ruleOpt.isEmpty()) {
            log.error("No pricing rule found for customer: {}, product: {}", dto.getCustomerCode(), dto.getProductCode());
            return Optional.empty();
        }
        PricingRule rule = ruleOpt.get();
        // 运费计算
        BigDecimal freight = freightCalculationService.calculateFreight(
                dto.getCustomerCode(),
                rule,
                dto.getChargeableWeight()
        );
        // 进行汇率转换 (如果货币不同)
        BigDecimal exchangeRate = EXCHANGE_RATES.getOrDefault(rule.getCurrency(), BigDecimal.ONE);
        freight = freight.multiply(exchangeRate);

        if ("AJEX784".equals(dto.getCustomerCode()) || "AJ402787000004".equals(dto.getCustomerCode())) {
            freight = freight.add(BigDecimal.valueOf(5));
        }
        if ("AJCN63".equals(dto.getCustomerCode())) {
            freight = freight.add(BigDecimal.valueOf(0.27).multiply(EXCHANGE_RATES.getOrDefault("USD", BigDecimal.ONE)));
        }
        if ("AJ288445000001".equals(dto.getCustomerCode())) {
            freight = freight.add(BigDecimal.valueOf(2).multiply(dto.getChargeableWeight()));
        }


        if ("AJEX850".equals(dto.getCustomerCode())&&(dto.getConsigneeCountry().equals("BAHRAIN")||dto.getConsigneeCountry().equals("BHR"))) {
            freight = freight.add(BigDecimal.valueOf(0.25).multiply(dto.getChargeableWeight()).multiply(EXCHANGE_RATES.getOrDefault("USD", BigDecimal.ONE)));
        }
        if ("AJEX850".equals(dto.getCustomerCode())&&(dto.getConsigneeCountry().equals("KUWAIT")||dto.getConsigneeCountry().equals("KWT"))) {
            freight = freight.add(BigDecimal.valueOf(1.7).multiply(EXCHANGE_RATES.getOrDefault("USD", BigDecimal.ONE)));
        }
        if ("AJEX850".equals(dto.getCustomerCode())&&(dto.getConsigneeCountry().equals("UNITED ARAB EMIRATES")||dto.getConsigneeCountry().equals("ARE"))) {
            freight = freight.add(BigDecimal.valueOf(0.32).multiply(dto.getChargeableWeight()).multiply(EXCHANGE_RATES.getOrDefault("USD", BigDecimal.ONE)));
        }

        if ("AJEX1542".equals(dto.getCustomerCode())&&dto.getConsigneeCountry().equals("SAU")) {
            List<String> remoteCities = new ArrayList<>(Arrays.asList(
                    "HURAYMILA",
                    "DAMMAM",
                    "RIYADH",
                    "JEDDAH",
                    "ES01",
                    "SS",
                    "AL MAJMA'",
                    "THADIQ",
                    "AL MAJMA'AH"
            ));
            if(remoteCities.contains(dto.getConsigneeCity())){
                freight = freight.add(BigDecimal.valueOf(1.1).multiply(EXCHANGE_RATES.getOrDefault("USD", BigDecimal.ONE)));
            }
        }




        order.setFreight(freight);


        // COD费用计算
        if (dto.getCodAmount() != null && dto.getCodAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (Strings.isBlank(dto.getCodCurrency())) {
                String country = null;
                if (dto.getStatus().equals("RTO Delivered") || dto.getStatus().equals("rto_delivered")) {
                    country = dto.getSenderCountry();
                }

                if (dto.getStatus().equals("Delivered") || dto.getStatus().equals("delivered")) {
                    country = dto.getConsigneeCountry();
                }
                if (country != null) {
                    dto.setCodCurrency(COUNTRY_CURRENCY_MAP.getOrDefault(country, "UNKNOWN"));
                }
            }

            BigDecimal codAmountInSAR = convertToSAR(dto.getCodAmount(), dto.getCodCurrency());

            BigDecimal codFee = freightCalculationService.calculateCODFee(
                    rule,
                    codAmountInSAR
            );
            codFee = convertToSAR(codFee, rule.getCurrency());


            order.setCodFee(codFee);


        }




        return Optional.of(order);
    }

    public static BigDecimal convertToSAR(BigDecimal amount, String fromCurrency) {
        if (amount == null || fromCurrency == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = EXCHANGE_RATES.getOrDefault(fromCurrency, BigDecimal.ONE);
        return amount.multiply(rate);
    }
}
