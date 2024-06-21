package com.stockmate.batch.fmp.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FmpClientConfig {

    @Value("${fmp.apikey}")
    private String apiKey;

    @Bean("fmpRequestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return template -> template.query("apikey", apiKey);
    }

}
