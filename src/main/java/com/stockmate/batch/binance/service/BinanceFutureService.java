package com.stockmate.batch.binance.service;

import com.stockmate.batch.binance.feign.BinanceClient;
import com.stockmate.batch.binance.feign.dto.AccountInfo;
import com.stockmate.batch.binance.feign.dto.OrderResponse;
import com.stockmate.batch.quant.dto.BTCTradeRequestDTO;
import com.stockmate.batch.util.BinanceUtil;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BinanceFutureService {

    private final BinanceClient binanceClient;

    public AccountInfo getAccountInfo() {
        return binanceClient.getAccountInfo(BinanceUtil.getNowTimeUTC());
    }

    public OrderResponse makePosition(String symbol, String side, String type, BigDecimal quantity) {
        long timestamp = BinanceUtil.getNowTimeUTC();
        return binanceClient.makePosition(timestamp, symbol, side, type, quantity, "RESULT");
    }

    public OrderResponse order(BTCTradeRequestDTO btcTradeRequestDTO) {
        return makePosition(btcTradeRequestDTO.getSymbol(), btcTradeRequestDTO.getSide(),
            "MARKET", btcTradeRequestDTO.getQuantity());
    }
}
