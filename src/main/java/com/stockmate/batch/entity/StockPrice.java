package com.stockmate.batch.entity;

import com.stockmate.batch.entity.base.BaseTimeEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice extends BaseTimeEntity implements Persistable<StockDateId> {

    @EmbeddedId
    private StockDateId id;

    @MapsId("code")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code")
    private Stock stock;
    private double open;
    private double high;
    private double low;
    private double close;
    private double adjClose;
    private long volume;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockPrice)) {
            return false;
        }
        StockPrice that = (StockPrice) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean isNew() {
        return this.getCreatedDate() == null;
    }
}
