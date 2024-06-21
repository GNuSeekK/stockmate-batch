package com.stockmate.batch.fmp.feign.dto;

import com.stockmate.batch.entity.Stock;
import lombok.Getter;

@Getter
public class StockSymbolDto {

    private String symbol;
    private String name;
    private double price;
    private String exchange;
    private String exchangeShortName;
    private String type;

    public boolean isNasDaq() {
        return exchangeShortName.equals("NASDAQ");
    }

    public boolean isNyse() {
        return exchangeShortName.equals("NYSE");
    }

    public boolean notNullExchange() {
        return exchangeShortName != null;
    }

    public boolean filterByExchange() {
        return notNullExchange() && (isNasDaq() || isNyse());
    }

    public Stock toEntity() {
        return Stock.builder()
            .code(symbol)
            .name(name)
            .exchange(exchangeShortName)
            .build();
    }
}