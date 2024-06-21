package com.stockmate.batch.fmp.feign.dto;

import com.stockmate.batch.entity.StockDateId;
import com.stockmate.batch.entity.StockPrice;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PriceData {

    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double adjClose;
    private long volume;

    public StockPrice toEntity(String code) {
        return StockPrice.builder()
            .id(StockDateId.builder()
                .code(code)
                .date(LocalDate.parse(date))
                .build())
            .open(open)
            .high(high)
            .low(low)
            .close(close)
            .adjClose(adjClose)
            .volume(volume)
            .build();
    }
}
