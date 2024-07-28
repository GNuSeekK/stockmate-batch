package com.stockmate.batch.binance.feign;

import feign.RequestInterceptor;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceClientConfig {

    @Value("${binance.future.secretKey}")
    private String secretKey;
    @Value("${binance.future.apiKey}")
    private String apiKey;

    @Bean
    public RequestInterceptor binanceRequestInterceptor() {
        return template -> {
            try {
                // 쿼리 파라미터를 정렬하고 문자열로 합치기
                StringBuilder payload = new StringBuilder();
                for (Map.Entry<String, Collection<String>> queryParam : template.queries().entrySet()) {
                    if (!payload.isEmpty()) {
                        payload.append("&");
                    }
                    payload.append(queryParam.getKey()).append("=").append(String.join(",", queryParam.getValue()));
                }

                // HMAC SHA256 서명 생성
                Mac sha256Hmac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
                sha256Hmac.init(secretKeySpec);
                byte[] hash = sha256Hmac.doFinal(payload.toString().getBytes(StandardCharsets.UTF_8));
                StringBuilder signature = new StringBuilder();
                for (byte b : hash) {
                    signature.append(String.format("%02x", b));
                }

                // 생성된 서명을 요청에 추가
                template.query("signature", signature.toString());
                template.header("X-MBX-APIKEY", apiKey);

            } catch (Exception e) {
                throw new RuntimeException("Failed to generate HMAC signature", e);
            }
        };
    }
}
