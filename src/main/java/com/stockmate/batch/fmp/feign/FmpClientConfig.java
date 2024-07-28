package com.stockmate.batch.fmp.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

//@Configuration
public class FmpClientConfig {

    @Value("${fmp.apikey}")
    private String apiKey;

    @Bean
    public RequestInterceptor fmpRequestInterceptor() {
        return template -> template.query("apikey", apiKey);
    }

}
