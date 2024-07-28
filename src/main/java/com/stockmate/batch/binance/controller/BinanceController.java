package com.stockmate.batch.binance.controller;

import com.stockmate.batch.binance.feign.dto.AccountInfo;
import com.stockmate.batch.binance.feign.dto.OrderResponse;
import com.stockmate.batch.binance.service.BinanceFutureService;
import com.stockmate.batch.util.BinanceUtil;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/binance")
@RequiredArgsConstructor
public class BinanceController {

    private final BinanceFutureService binanceFutureService;

    @GetMapping("/account")
    public AccountInfo getAccountInfo() {
        return binanceFutureService.getAccountInfo();
    }

    @PostMapping("/buy")
    public OrderResponse buyCoin() {
        String symbol = BinanceUtil.COIN;
        BigDecimal quantity = new BigDecimal("0.002");
        String side = "SELL";
        String type = "MARKET";
        return binanceFutureService.makePosition(symbol, side, type, quantity);
    }
}
