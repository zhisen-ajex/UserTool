/*
package com.verify.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.verify.entity.WhatsappShipperInfo;
import com.verify.repository.WhatsappShipperInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShipperInfoService {

    @Autowired
    private WhatsappShipperInfoRepository repository;

    private final Map<String, String> unicodeEncodedMap = new LinkedHashMap<>();
    // 在类中添加ObjectMapper（可复用）
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String getFormattedShipperNames() throws JsonProcessingException {
        List<WhatsappShipperInfo> all = repository.findAll();
        Map<String, Object> resultMap = new LinkedHashMap<>();

        for (WhatsappShipperInfo info : all) {
            String accountNo = info.getAccountNo();
            if (accountNo == null) continue;

            // 处理英文名称
            if (info.getNameEn() != null) {
                String key = accountNo + "_en_GB";
                String value = info.getNameEn();
                // 可选：手动处理特殊逻辑（如仅编码阿拉伯语）
                String encodedValue = toUnicode(value);
                resultMap.put(key, encodedValue);
                if (!encodedValue.equals(value)) {
                    unicodeEncodedMap.put(key, value);
                }
            }

            // 处理阿拉伯语名称
            if (info.getNameAr() != null) {
                String key = accountNo + "_ar";
                String original = info.getNameAr();
                String encoded = toUnicode(original);
                resultMap.put(key, encoded);
                if (!encoded.equals(original)) {
                    unicodeEncodedMap.put(key, original);
                }
            }
        }

        // 使用ObjectMapper自动序列化，确保正确转义
        return objectMapper.writeValueAsString(resultMap);
    }

    // 保留toUnicode方法用于自定义编码逻辑（如仅编码阿拉伯语）
    private String toUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c > 127) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }



}

*/


package com.verify.services;

import com.verify.entity.WhatsappShipperInfo;
import com.verify.repository.WhatsappShipperInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipperInfoService {

    @Autowired
    private WhatsappShipperInfoRepository repository;
    public String getFormattedShipperNamesForProperties() {
        List<WhatsappShipperInfo> all = repository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (WhatsappShipperInfo info : all) {
            String accountNo = info.getAccountNo();
            if (accountNo == null) continue;

            if (info.getNameEn() != null) {
                String key = accountNo + "_en_GB";
                String encodedValue = toUnicode(info.getNameEn());

                if (!first) sb.append(",");
                sb.append("\"").append(key).append("\":\"").append(encodedValue).append("\"");
                first = false;
            }

            if (info.getNameAr() != null) {
                String key = accountNo + "_ar";
                String encodedValue = toUnicode(info.getNameAr());

                if (!first) sb.append(",");
                sb.append("\"").append(key).append("\":\"").append(encodedValue).append("\"");
                first = false;
            }
        }

        sb.append("}");
        return sb.toString(); // 可以直接复制到 application.properties
    }


    private String toUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c > 127) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
