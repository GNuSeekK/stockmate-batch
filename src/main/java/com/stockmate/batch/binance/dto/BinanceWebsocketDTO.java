package com.stockmate.batch.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BinanceWebsocketDTO {

    @JsonProperty("e")
    private String eventType;
    @JsonProperty("E")
    private Long eventTime;
    @JsonProperty("s")
    private String symbol;
    @JsonProperty("a")
    private Long aggregateTradeId;
    @JsonProperty("p")
    private String price;
    @JsonProperty("q")
    private String quantity;
    @JsonProperty("f")
    private Long firstTradeId;
    @JsonProperty("l")
    private Long lastTradeId;
    @JsonProperty("T")
    private Long tradeTime;
    @JsonProperty("m")
    private Boolean buyerMarketMaker;

    @Override
    public String toString() {
        return "BinanceWebsocketDTO{" +
            "eventType='" + eventType + '\'' +
            ", eventTime=" + eventTime +
            ", symbol='" + symbol + '\'' +
            ", aggregateTradeId=" + aggregateTradeId +
            ", price='" + price + '\'' +
            ", quantity='" + quantity + '\'' +
            ", firstTradeId=" + firstTradeId +
            ", lastTradeId=" + lastTradeId +
            ", tradeTime=" + tradeTime +
            ", buyerMarketMaker=" + buyerMarketMaker +
            '}';
    }
}
