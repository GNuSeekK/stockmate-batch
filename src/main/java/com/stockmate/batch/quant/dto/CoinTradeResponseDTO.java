package com.stockmate.batch.quant.dto;

import com.stockmate.batch.binance.feign.dto.OrderResponse;
import com.stockmate.batch.util.BinanceUtil;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class CoinTradeResponseDTO {

    private String symbol;
    private long time;
    private double price;
    private double totalPrice;
    private BigDecimal amount;
    private double fee;
    private long nanoTime;

    public CoinTradeResponseDTO(OrderResponse orderResponse) {
        this.symbol = orderResponse.getSymbol();
        this.time = orderResponse.getUpdateTime();
        this.price = Double.parseDouble(orderResponse.getAvgPrice());
        this.totalPrice = Double.parseDouble(orderResponse.getCumQuote());
        this.amount = new BigDecimal(orderResponse.getExecutedQty());
        this.fee = this.totalPrice * BinanceUtil.FEE;
        this.nanoTime = orderResponse.getNanoTime();
    }
}
