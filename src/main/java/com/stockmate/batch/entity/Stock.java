package com.stockmate.batch.entity;

import com.stockmate.batch.entity.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
public class Stock extends BaseTimeEntity implements Persistable<String> {

    @Id
    @Column(name = "stock_code")
    private String code;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "korean_name")
    private String koreanName;

    @Column(name = "exchange")
    private String exchange;

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }


    @Override
    public String getId() {
        return this.getCode();
    }

    @Override
    public boolean isNew() {
        return this.getCode() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        Stock stock = (Stock) o;
        return Objects.equals(getCode(), stock.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode());
    }
}
