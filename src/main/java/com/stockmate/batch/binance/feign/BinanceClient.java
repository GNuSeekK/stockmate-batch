package com.stockmate.batch.binance.feign;

import com.stockmate.batch.binance.feign.dto.AccountInfo;
import com.stockmate.batch.binance.feign.dto.OrderResponse;
import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "binanceFuture", url = "${binance.future.url}", configuration = BinanceClientConfig.class)
public interface BinanceClient {

    @GetMapping("/fapi/v2/account")
    AccountInfo getAccountInfo(@RequestParam("timestamp") long timestamp);

    @PostMapping("/fapi/v1/order")
    OrderResponse makePosition(
        @RequestParam("timestamp") long timestamp,
        @RequestParam("symbol") String symbol,
        @RequestParam("side") String side,
        @RequestParam("type") String type,
        @RequestParam("quantity") BigDecimal quantity,
        @RequestParam("newOrderRespType") String newOrderRespType);

}
