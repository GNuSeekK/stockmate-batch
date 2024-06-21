package com.stockmate.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
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
public class StockDateId implements Serializable {

    /**
     * TODO : 현재 hibernate 설정에 따라, 복합키가 자동으로 알파벳 순으로 생성되지만, 추후 DabaBase에서 DDL을 통해 생성할 때는 알파벳 순서가 아닌 (stock_code, original_date)로 변경해서 테스트 필요
     */
    @Column(name = "stock_code")
    private String code;

    @Column(name = "original_date")
    private LocalDate date;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockDateId)) {
            return false;
        }
        StockDateId that = (StockDateId) o;
        return Objects.equals(getCode(), that.getCode()) && Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getDate());
    }
}
