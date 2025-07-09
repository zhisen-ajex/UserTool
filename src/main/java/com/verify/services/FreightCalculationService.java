package com.verify.services;

import com.verify.entity.PricingRule;
import com.verify.entity.ServiceType;
import com.verify.entity.ShippingRate;
import com.verify.repository.ShippingRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreightCalculationService {
    // 模拟汇率缓存（实际项目中建议从数据库或API获取）
    private static final Map<String, BigDecimal> exchangeRates = new ConcurrentHashMap<>();

    static {
        // 假设的汇率（示例数据，实际情况需定期更新）
        exchangeRates.put("USD_TO_SAR", BigDecimal.valueOf(3.75)); // 1 USD = 3.75 SAR
        exchangeRates.put("USD_TO_AED", BigDecimal.valueOf(3.67)); // 1 USD = 3.67 AED
        exchangeRates.put("USD_TO_CNY", BigDecimal.valueOf(7.1));  // 1 USD = 7.1 CNY
        exchangeRates.put("USD_TO_BHD", BigDecimal.valueOf(0.38)); // 1 USD = 0.38 BHD
        exchangeRates.put("USD_TO_KWD", BigDecimal.valueOf(0.31)); // 1 USD = 0.31 KWD

        // 反向计算（SAR、AED、CNY、BHD、KWD 转 USD）
        exchangeRates.put("SAR_TO_USD", BigDecimal.ONE.divide(exchangeRates.get("USD_TO_SAR"), 6, RoundingMode.HALF_UP));
        exchangeRates.put("AED_TO_USD", BigDecimal.ONE.divide(exchangeRates.get("USD_TO_AED"), 6, RoundingMode.HALF_UP));
        exchangeRates.put("CNY_TO_USD", BigDecimal.ONE.divide(exchangeRates.get("USD_TO_CNY"), 6, RoundingMode.HALF_UP));
        exchangeRates.put("BHD_TO_USD", BigDecimal.ONE.divide(exchangeRates.get("USD_TO_BHD"), 6, RoundingMode.HALF_UP));
        exchangeRates.put("KWD_TO_USD", BigDecimal.ONE.divide(exchangeRates.get("USD_TO_KWD"), 6, RoundingMode.HALF_UP));
    }

    private final ShippingRateRepository shippingRateRepository;

    public BigDecimal calculateFreight(String customerCode, PricingRule rule, BigDecimal weight) {

        if ("AJCN711".equals(customerCode)) {
            return calculateFreightForAJCN711(weight);
        }


        BigDecimal totalFreight = rule.getBasePrice();
        BigDecimal extraWeight = weight.subtract(rule.getBaseWeight());


        if (extraWeight.compareTo(BigDecimal.ZERO) > 0) {
            if (rule.getExtraPricePer0_1kg() != null) {
                int units = extraWeight.divide(BigDecimal.valueOf(0.1), 0, BigDecimal.ROUND_CEILING).intValue();
                totalFreight = totalFreight.add(rule.getExtraPricePer0_1kg().multiply(BigDecimal.valueOf(units)));
            } else if (rule.getExtraPricePer0_5kg() != null) {
                int units = extraWeight.divide(BigDecimal.valueOf(0.5), 0, BigDecimal.ROUND_CEILING).intValue();
                totalFreight = totalFreight.add(rule.getExtraPricePer0_5kg().multiply(BigDecimal.valueOf(units)));
            } else if (rule.getExtraPricePerKg() != null) {
                totalFreight = totalFreight.add(rule.getExtraPricePerKg().multiply(extraWeight));
            }
        }

        return totalFreight;
    }

    /**
     * Customs Clearance: $0.32 USD/kg = 1.2 SAR/Kg
     * <p>
     * •  Prepaid (PPD):
     * •	2.5–5 kg: $1.80 USD = 6.75 SAR
     * •	Additional kg: $0.12 USD/kg = 0.45 SAR/Kg
     * •  Cash on Delivery (COD):
     * •	2–5 kg: $2.40 USD = 9 SAR
     * •	Additional kg: $0.12 USD/kg = 0.45 SAR/Kg
     *
     * @param weight
     * @param serviceType
     * @return
     */
    public static BigDecimal calculateUAEFreight(BigDecimal weight, ServiceType serviceType) {
        BigDecimal shippingCost = null;

        if (serviceType == ServiceType.PPD) {
            shippingCost = BigDecimal.valueOf(6.75); // base price for PPD > 5kg
        } else if (serviceType == ServiceType.COD) {
            shippingCost = BigDecimal.valueOf(9); // base price for COD or PPD <= 5kg
        }

        // 超出 5kg 部分每 kg 加 0.45
        BigDecimal excessWeight = weight.compareTo(BigDecimal.valueOf(5)) > 0
                ? weight.subtract(BigDecimal.valueOf(5))
                : BigDecimal.ZERO;

        shippingCost = shippingCost.add(excessWeight.multiply(BigDecimal.valueOf(0.45)));

        // 所有运费统一加重量 * 1.2
        shippingCost = shippingCost.add(weight.multiply(BigDecimal.valueOf(1.2))); //Customs Clearance: $0.32 USD/kg = 1.2 SAR/Kg
        return shippingCost;
    }

    public static void main(String[] args) {
        // 测试用例 1：PPD，重量 3kg
        BigDecimal freight1 = calculateUAEFreight(BigDecimal.valueOf(3), ServiceType.PPD);
        System.out.println("PPD - 3kg 运费: " + freight1);

        // 测试用例 2：PPD，重量 7kg
        BigDecimal freight2 = calculateUAEFreight(BigDecimal.valueOf(7), ServiceType.PPD);
        System.out.println("PPD - 7kg 运费: " + freight2);

        // 测试用例 3：COD，重量 4kg
        BigDecimal freight3 = calculateUAEFreight(BigDecimal.valueOf(4), ServiceType.COD);
        System.out.println("COD - 4kg 运费: " + freight3);

        // 测试用例 4：COD，重量 6kg
        BigDecimal freight4 = calculateUAEFreight(BigDecimal.valueOf(6), ServiceType.COD);
        System.out.println("COD - 6kg 运费: " + freight4);
    }


    public BigDecimal calculateFreight(String countryCode, ServiceType serviceType, BigDecimal weight) {
        Optional<ShippingRate> tempRate = shippingRateRepository.findSuitableRate(countryCode, serviceType, weight);
        if (tempRate.isEmpty()) {
            weight = BigDecimal.valueOf(10);
        }

        BigDecimal finalWeight = weight;
        ShippingRate rate = shippingRateRepository.findSuitableRate(countryCode, serviceType, weight)
                .orElseThrow(() -> {
                    log.warn("No suitable shipping rate found for countryCode={}, serviceType={}, weight={}",
                            countryCode, serviceType, finalWeight);
                    return new IllegalArgumentException("No suitable shipping rate found");
                });
        BigDecimal shippingCost;

        if (serviceType == ServiceType.COD) {
            // COD 计算：基础价格 + 超重费用
            shippingCost = rate.getBasePrice();
            if (weight.compareTo(rate.getWeightMax()) > 0) {
                BigDecimal excessWeight = weight.subtract(rate.getWeightMax());
                shippingCost = shippingCost.add(excessWeight.multiply(rate.getExtraPerKg()));
            }

        } else {
            // PPD 计算：直接使用 base_price
            shippingCost = rate.getBasePrice();
        }

        // 计算清关费用（所有类型都要）
        BigDecimal clearanceFee = weight.multiply(rate.getClearancePerKg());
        shippingCost = shippingCost.add(clearanceFee);

        return shippingCost.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateFreightForAJCN711(BigDecimal weight) {
        if (weight.compareTo(BigDecimal.valueOf(2)) <= 0) {
            return BigDecimal.valueOf(14);
        } else if (weight.compareTo(BigDecimal.valueOf(10)) <= 0) {
            return BigDecimal.valueOf(16);
        } else {
            BigDecimal extraWeight = weight.subtract(BigDecimal.valueOf(10));
            int units = extraWeight.divide(BigDecimal.valueOf(0.1), 0, BigDecimal.ROUND_CEILING).intValue();
            return BigDecimal.valueOf(16).add(BigDecimal.valueOf(0.5).multiply(BigDecimal.valueOf(units)));
        }
    }

    public BigDecimal calculateCODFee(PricingRule rule, BigDecimal codAmount) {
        BigDecimal codFee;

        if ("PERCENTAGE".equals(rule.getCodFeeType())) {
            codFee = codAmount.multiply(rule.getCodFeeValue().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));

            // 将计算的 COD 费用转换为 USD 进行最小值比较
            BigDecimal codFeeInUSD = convertToUSD(codFee, rule.getCurrency());

            // 确保 COD 费用最小为 1.1 USD
            if (codFeeInUSD.compareTo(BigDecimal.valueOf(1.1)) < 0) {
                codFeeInUSD = BigDecimal.valueOf(1.1);
            }

            // 返回调整后的 COD 费用，并转换回原始货币
            return convertFromUSD(codFeeInUSD, rule.getCurrency());
        } else if ("FIXED".equals(rule.getCodFeeType())) {
            return rule.getCodFeeValue();
        } else {
            return BigDecimal.ZERO;
        }

    }

    private BigDecimal convertToUSD(BigDecimal amount, String currency) {
        if ("USD".equals(currency)) {
            return amount;
        }
        // 调用你的汇率转换逻辑
        return convert(amount, currency, "USD");
    }

    private BigDecimal convertFromUSD(BigDecimal amount, String currency) {
        if ("USD".equals(currency)) {
            return amount;
        }
        // 调用你的汇率转换逻辑
        return convert(amount, "USD", currency);
    }

    /**
     * 货币转换
     *
     * @param amount       需要转换的金额
     * @param fromCurrency 源货币
     * @param toCurrency   目标货币
     * @return 转换后的金额
     */
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        String key = fromCurrency + "_TO_" + toCurrency;
        BigDecimal rate = exchangeRates.getOrDefault(key, BigDecimal.ONE); // 默认不变
        return amount.multiply(rate).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 更新汇率（可用于定期同步）
     *
     * @param fromCurrency 源货币
     * @param toCurrency   目标货币
     * @param rate         新的汇率
     */
    public void updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate) {
        exchangeRates.put(fromCurrency + "_TO_" + toCurrency, rate);
        exchangeRates.put(toCurrency + "_TO_" + fromCurrency, BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP));
    }
}
