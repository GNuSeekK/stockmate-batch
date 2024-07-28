package com.stockmate.batch.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TradeLogId implements Serializable {

    private long id;
    private String symbol;
    private long buyTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TradeLogId that)) {
            return false;
        }
        return id == that.id && buyTime == that.buyTime && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, symbol, buyTime);
    }
}
