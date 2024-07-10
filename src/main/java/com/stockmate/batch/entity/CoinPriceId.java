package com.stockmate.batch.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CoinPriceId implements Serializable {

    private String symbol;
    private long openTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoinPriceId that)) {
            return false;
        }
        return openTime == that.openTime && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, openTime);
    }
}