package com.stockmate.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTickId implements Serializable {


    @Column(name = "stock_code")
    private String code;

    @Column(name = "original_date")
    private LocalDateTime date;

    @Column(name = "price")
    private int price;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockTickId that)) {
            return false;
        }
        return getPrice() == that.getPrice() && Objects.equals(getCode(), that.getCode())
            && Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getDate(), getPrice());
    }
}
