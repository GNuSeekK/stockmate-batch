package com.stockmate.batch.binance.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stockmate.batch.entity.Coin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinanceCoinPriceRequestDto {

    private String symbol;
    private long startTime;
    private long endTime;
    private String interval;
    @JsonIgnore
    private Coin coin;

}
