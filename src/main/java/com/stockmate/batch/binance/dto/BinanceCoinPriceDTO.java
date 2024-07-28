package com.stockmate.batch.binance.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.stockmate.batch.binance.util.BinanceCoinPriceDTODeserializer;
import com.stockmate.batch.entity.CoinPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = BinanceCoinPriceDTODeserializer.class)
public class BinanceCoinPriceDTO {

    @JsonProperty("0")
    private long openTime; // 초단위로 저장

    @JsonProperty("1")
    private double openPrice;

    @JsonProperty("2")
    private double highPrice;

    @JsonProperty("3")
    private double lowPrice;

    @JsonProperty("4")
    private double closePrice;

    @JsonProperty("5")
    private double volume;

    @JsonProperty("7")
    private double quoteAssetVolume;

    @JsonProperty("8")
    private int numberOfTrades;

    public CoinPrice toEntity(BinanceCoinPriceRequestDto requestDto) {
        return CoinPrice.builder()
            .id(requestDto.getCoin().getId())
            .openTime(this.openTime)
            .openPrice(this.openPrice)
            .highPrice(this.highPrice)
            .lowPrice(this.lowPrice)
            .closePrice(this.closePrice)
            .volume(this.volume)
            .quoteAssetVolume(this.quoteAssetVolume)
            .numberOfTrades(this.numberOfTrades)
            .build();
    }
}

