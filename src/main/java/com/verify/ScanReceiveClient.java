package com.verify;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanReceiveClient {

    private static final String BEARER_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJnQWlSMDYyeUZVeGRZV0paVFpxNGRPaHU5MjMybUJQTEQzelFOckhJRlZrIn0.eyJleHAiOjE3NTMzMjM0NTUsImlhdCI6MTc1MzIzNzA1NSwiYXV0aF90aW1lIjoxNzUzMjM3MDUyLCJqdGkiOiJjYTRiNmYzMS0wNjY0LTQzZGMtODY0Yy1hYzJmYWEyMTZiZDUiLCJpc3MiOiJodHRwczovL2F1dGguYWotZXguY29tL3JlYWxtcy9hb25lIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjVkNzhiNDRkLWVlZTAtNGU5OS1hOGI1LWIyNjA4MTkxMjhjYiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFvbmUtcG9ydGFsIiwic2Vzc2lvbl9zdGF0ZSI6IjhhNzQwYjUyLWEwNmUtNDc4Ny04YmU0LTRlN2NlZTU5Y2YxNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9hb25lLmFqLWV4LmNvbSJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQU9ORV9BU1NFVFNfVkVISUNMRVMucmVhZCIsIkFPTkVfQ1NfQ09NUExBSU5ULnJlc29sdmVkIiwiQU9ORV9PUFNfQ09OU0lHTk1FTlRTX1BJQ0tVUC50YXNrcyIsIkFPTkVfQ09OU0lHTk1FTlRTX0NPTlNJR05NRU5ULnNlbnNpdGl2ZSIsIkFPTkVfT1BTX0FDTC53cml0ZSIsIkFPTkVfT1BTX0NPVVJJRVJTLnJlYWQiLCJBT05FX09QU19DT1VSSUVSUy5sb2NhdGlvbiIsIkFPTkVfT1BTX1JFQVNPTlNfTlBSLnJlYWQiLCJBT05FX09QU19SRUFTT05TX0hPTEQucmVhZCIsIkFPTkVfQVNTRVRTX1ZFSElDTEVTLmFkbWluIiwiQU9ORV9PUFNfVFJJUFNfT1BFUkFUT1JTLndyaXRlIiwiQU9ORV9PUFNfQ09OU0lHTk1FTlRTLnN1cHBsaWVyX29mZCIsIkFPTkVfT1BTX0NPTlNJR05NRU5UUy5yZWFkIiwiQU9ORV9PUFNfQ09VUklFUlMubWFwIiwidW1hX2F1dGhvcml6YXRpb24iLCJBT05FX09QU19DT05TSUdOTUVOVFMubG9zdCIsIkFPTkVfT1BTX0RSSVZFUlMud3JpdGUiLCJBT05FX09QU19MQVNULU1JTEUud3JpdGUiLCJBT05FX09QU19SRUFTT05TX0xJTkVIQVVMLnJlYWQiLCJBT05FX09QU19EUklWRVJTLnNlbnNpdGl2ZSIsIkFPTkVfT1BTX0xBU1QtTUlMRV9ERUxJVkVSWS1QRVJGT1JNQU5DRS5yZWFkIiwiQU9ORV9PUFNfTEFTVC1NSUxFX0RFTElWRVJZLVZBTElEQVRJT04uc3RhcnQiLCJBT05FX09QU19SVE8ucmVhZCIsIkFPTkVfT1BTX0xBU1QtTUlMRV9ERUJSSUVGSU5HLUhJU1RPUlkucmVhZCIsIkFPTkVfT1BTX0FDTC5hc3NpZ24iLCJBT05FX09QU19DT05TSUdOTUVOVFMuZGlzY3JlcGFuY3kiLCJBT05FX09QU19UUklQUy5yZWFkIiwiQU9ORV9DU19MT0NBVElPTi53cml0ZSIsIkFPTkVfT1BTX1BJQ0tVUC1SRVFVRVNULndyaXRlIiwiQU9ORV9PUFNfQ09OU0lHTk1FTlRTLnByaW50IiwiQU9ORV9SRVBPUlRTLm9wcyIsImRlZmF1bHQtcm9sZXMtYW9uZSIsIkFPTkVfQ1JNX0VNUExPWUVFUy5wcm9maWxlIiwiQU9ORV9PUFNfQ09OU0lHTk1FTlRTX0RFTElWRVJZLnRhc2tzIiwiQU9ORV9PUFNfVFJJUFMuYWRtaW4iLCJBT05FX09QU19DT05TSUdOTUVOVFMuaG9sZCIsIkFPTkVfT1BTX0NPTlNJR05NRU5UUy5kZWxpdmVyeSIsIkFPTkVfQ09OU0lHTk1FTlRTX0NPTlNJR05NRU5ULnJlYWQiLCJBT05FX0NPTlNJR05NRU5UU19DT05TSUdOTUVOVC5lZGl0X2Rlc3RpbmF0aW9uX2FkZHJlc3MiLCJBT05FX09QU19DT05TSUdOTUVOVFMucGlja3VwIiwiQU9ORV9PUFNfQ09OU0lHTk1FTlRTLnN1cHBsaWVyX2RlbGl2ZXJ5X2ZhaWxlZCIsIkFPTkVfT1BTX1JFQVNPTlNfTkRSLnJlYWQiLCJBT05FX09QU19DT05TSUdOTUVOVFMucnRvX29mZCIsIkFPTkVfT1BTX0RSSVZFUlMuYWRtaW4iLCJBT05FX09QU19DT05TSUdOTUVOVFMucmV2b2tlX2hvbGQiLCJBT05FX09QU19DT05TSUdOTUVOVFMuZGlzcG9zZWQiLCJvZmZsaW5lX2FjY2VzcyIsIkFPTkVfT1BTX1BJQ0tVUC1SRVFVRVNULmNhbmNlbCIsIkFPTkVfT1BTX1JPVVRFUy5yZWFkIiwiQU9ORV9PUFNfQUNMLnByaW50IiwiQU9ORV9PUFNfTEFTVC1NSUxFX0RFQlJJRUZJTkctUkVQT1JULnZlcmlmeSIsIkFPTkVfT1BTX0NPTlNJR05NRU5UUy5yZWNlaXZlIiwiQU9ORV9PUFNfQ09OU0lHTk1FTlRTLnJ0b19kZWxpdmVyZWQiLCJBT05FX09QU19BQ0wuYWRtaW4iLCJBT05FX09QU19DT05TSUdOTUVOVFMuZGFtYWdlZCIsIkFPTkVfT1BTX0xBU1QtTUlMRV9ERUJSSUVGSU5HLndyaXRlIiwiQU9ORV9PUFNfUElDS1VQLVJFUVVFU1QucGlja3VwIiwiQU9ORV9PUFNfVFJJUFNfRFJJVkVSUy53cml0ZSIsIkFPTkVfT1BTX0xBU1QtTUlMRV9ERUxJVkVSWS1WQUxJREFUSU9OLndyaXRlIiwiQU9ORV9TRVRUSU5HU19SRVNPVVJDRVMuY2hlY2stdXBncmFkZSIsIkFPTkVfT1BTLmFkbWluIiwiQU9ORV9PUFNfUk9VVEVTLndyaXRlIiwiQU9ORV9PUFNfTEFTVC1NSUxFX0RFTElWRVJZLVZBTElEQVRJT04udmFsaWRhdGUiLCJBT05FX09QU19ERUxJVkVSWS1GTE9XLnJlYWQiLCJBT05FX09QU19XRUlHSElORy53cml0ZSIsIkFPTkVfT1BTX0xBU1QtTUlMRV9ERUxJVkVSWS1UQVNLUy53cml0ZSIsIkFPTkVfT1BTX1RSSVBTLndyaXRlIiwiQU9ORV9GSU5BTkNFX0xFREdFUlNfQ09MTEVDVElPTlMuY29sbGVjdCIsIkFPTkVfT1BTX1JUTy53cml0ZSIsIkFPTkVfT1BTX0NPTlNJR05NRU5UUy5zdXBwbGllcl9kZWxpdmVyZWQiLCJBT05FX09QU19DT1VSSUVSUy53cml0ZSIsIkFPTkVfT1BTX0ZMQUdTLnByaW50Il19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBhdHRyaWJ1dGVzIGVtYWlsIiwic2lkIjoiOGE3NDBiNTItYTA2ZS00Nzg3LThiZTQtNGU3Y2VlNTljZjE1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJIdWkgUWl1IiwiYXR0cmlidXRlcyI6eyJodWJDb2RlIjoiU1pYU1pYIiwiYXJlYXMiOlsiQ0hOLGFsbCJdLCJlbXBsb3llZUNvZGUiOiI0MDAzNSJ9LCJwcmVmZXJyZWRfdXNlcm5hbWUiOiI0MDAzNSIsImdpdmVuX25hbWUiOiJIdWkiLCJmYW1pbHlfbmFtZSI6IlFpdSIsImVtYWlsIjoiaHVpLnFpdUBhai1leC5jb20ifQ.uS2agSTQQwwQUge_SqZruTifm1SPxEtzEQayIuhDF6A2SxGoKZX4A8U8jV0Ti5q_prT4nFFdVyoecBg-Gj4sVTqf-KuMelXMeOnis8s90twcJtACsDjwc76Lzn8FWB-OOFXGCXzXo63w-JOXg-VLQictmJe0onP2okoAGP2dY_qmTkowlEnhzUAAFjQ5_i09LFuUDu0FGDqx4qDAykmRbS9o6Lfg7ceqfvoH_5mlQBEWKQOYnjM2QNS5wWBzjwd86iVxaMxwiwz9sCtCFe8G_yU2hvJywVZexaWYfAlXKRk0hgjdnY-AZif0KRHGz-PB-KGWUS_5ovyeUCEflceaPA";


    private static final String DELETE_CACHE_URL = "https://api-aone.aj-ex.com/ops/api/v1/consignments/cache/";

    //40035
    //Ajex@12345678
    public static void main(String[] args) {
        String SCAN_RECEIVE_URL = "https://api-aone.aj-ex.com/ops/api/v1/scans/receive";
        List<String> trackingIds = List.of(
                "AJA100003404838",
                "AJA100003406205",
                "AJA100003402947",
                "AJA100003406202",
                "AJA100003394456",
                "AJA100003406187",
                "AJA100003395980",
                "AJA100003402244",
                "AJA100003395933",
                "AJA100003406518",
                "AJA100003402942",
                "AJA100003390198",
                "AJA100003400284",
                "AJA100003405851",
                "AJA100003394308",
                "AJA100003403000",
                "AJA100003405592",
                "AJA100003409622",
                "AJA100003394627",
                "AJA100003404875",
                "AJA100003390319",
                "AJA100003409618",
                "AJA100003406775",
                "AJA100003408952",
                "AJA100003405355",
                "AJA100003407759",
                "AJA100003392423",
                "AJA100003409974",
                "AJA100003406529",
                "AJA100003411636",
                "AJA100003392471",
                "AJA100003415384",
                "AJA100003409635",
                "AJA100003402199",
                "AJA100003415256",
                "AJA100003394431",
                "AJA100003415258",
                "AJA100003408175",
                "AJA100003409977",
                "AJA100003404344",
                "AJA100003407767",
                "AJA100003399429"
        );
        RestTemplate restTemplate = new RestTemplate();

        for (String trackingId : trackingIds) {
            Map<String, Object> body = new HashMap<>();
            body.put("eventCode", 303);
            body.put("trackingId", trackingId);
            body.put("employeeCode", "40035");
            body.put("thirdPartyCode", null);
            body.put("latitude", null);
            body.put("longitude", null);
            body.put("hubCode", "SZXSZX");
            body.put("images", null);
            body.put("eventDate", 1753258645000L); // 示例时间戳
            body.put("eventTimeZone", "CST");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.ALL));
            headers.setBearerAuth(BEARER_TOKEN);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    /*        // 3. POST 成功后删除缓存
            String deleteUrl = DELETE_CACHE_URL + trackingId;
            HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
            ResponseEntity<Void> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteRequest, Void.class);

            if (deleteResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println(trackingId + " 🧹 Cache cleared.");
            } else {
                System.err.println(trackingId + " ⚠️ Cache clear failed: " + deleteResponse.getStatusCode());
            }*/
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(SCAN_RECEIVE_URL, requestEntity, String.class);
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
