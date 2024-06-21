package com.stockmate.batch.hankook.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "hankook", url = "${hankook.url}", configuration = HankookClientConfig.class)
public interface HankookClient {

}
