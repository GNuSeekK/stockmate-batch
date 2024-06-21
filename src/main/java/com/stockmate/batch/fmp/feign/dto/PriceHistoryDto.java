package com.stockmate.batch.fmp.feign.dto;

import com.stockmate.batch.entity.StockPrice;
import java.util.List;
import lombok.Getter;

@Getter
public class PriceHistoryDto {

    private String symbol;
    private List<PriceData> historical;

    public List<StockPrice> toEntityList() {
        return historical.stream()
            .map(priceData -> priceData.toEntity(symbol))
            .toList();
    }
}
