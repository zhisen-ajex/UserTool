package com.verify.yuzhu;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickupClient {


    //40035
    //Ajex@12345678
    public static void main(String[] args) {
        String PICKUP_URL = "https://api-aone.aj-ex.com/ops/api/v1/scans/bulk";

        RestTemplate restTemplate = new RestTemplate();

        for (String trackingId : CommonParam.trackingIds) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.ALL));
            headers.setBearerAuth(CommonParam.BEARER_TOKEN);
            Map<String, Object> body = new HashMap<>();
            body.put("eventCode", 210);
            body.put("trackingId", trackingId);
            body.put("employeeCode", "40035");
            body.put("thirdPartyCode", null);
            body.put("latitude", null);
            body.put("longitude", null);
            body.put("hubCode", "SZXSZX");
            body.put("images", null);
            body.put("sortingMachine", true);
            body.put("eventDate", Instant.now().toEpochMilli()); // 示例时间戳
            body.put("eventTimeZone", "GMT+8");



            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(PICKUP_URL, requestEntity, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println(trackingId + " ✅ Scan event posted successfully.");
                } else {
                    System.err.println(trackingId + " ❌ Failed: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.err.println(trackingId + " ❌ Error: " + e.getMessage());
            }
        }
    }




}
