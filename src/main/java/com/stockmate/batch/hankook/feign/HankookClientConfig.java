package com.stockmate.batch.hankook.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HankookClientConfig {

    @Value("${hankook.appKey}")
    private String appKey;
    @Value("${hankook.secretKey}")
    private String secretKey;
    private String accessToken;
    private LocalDateTime tokenExpireTime;

    @Value("${hankook.url}")
    private String url;

    @Bean("hankookRequestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("appkey", appKey);
            template.header("appsecret", secretKey);
            template.header("authorization", getAccessToken());
        };
    }

    private String getAccessToken() {
        // accessToken이 null 이거나 만료 시간 1시간 전일 경우
        if (accessToken == null || tokenExpireTime.isBefore(LocalDateTime.now().minusHours(1))) {
            // accessToken 발급 로직
            String requestUrl = this.url + "/oauth2/tokenP";
            Request request = getTokenRequest();

            RestTemplate restTemplate = new RestTemplate();
            Response response = restTemplate.postForObject(requestUrl, request, Response.class);
            this.accessToken = response.getAccessToken();
            this.tokenExpireTime = LocalDateTime.parse(response.getAccessTokenTokenExpired(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return "Bearer " + accessToken;
    }

    public String getApprovalToken() {
        String requestUrl = this.url + "/oauth2/Approval";
        SocketRequest request = getSocketRequest();

        try {
            System.out.println(new ObjectMapper().writeValueAsString(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        RestTemplate restTemplate = new RestTemplate();
        ApprovalResponse response = restTemplate.postForObject(requestUrl, request, ApprovalResponse.class);
        return response.getApprovalKey();
    }


    private Request getTokenRequest() {
        return Request.builder()
            .grantType("client_credentials")
            .appkey(appKey)
            .appsecret(secretKey)
            .build();
    }

    private SocketRequest getSocketRequest() {
        return SocketRequest.builder()
            .grantType("client_credentials")
            .appkey(appKey)
            .secretkey(secretKey)
            .build();
    }

    @Builder
    @Getter
    private static class Request {

        @JsonProperty("grant_type")
        private String grantType;
        private String appkey;
        private String appsecret;
    }

    @Getter
    private static class Response {

        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("access_token_token_expired")
        private String accessTokenTokenExpired;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("expires_in")
        private int expiresIn;
    }

    @Builder
    @Getter
    private static class SocketRequest {

        @JsonProperty("grant_type")
        private String grantType;
        private String appkey;
        private String secretkey;
    }

    @Getter
    private static class ApprovalResponse {

        @JsonProperty("approval_key")
        private String approvalKey;
    }
}
