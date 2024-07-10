package com.stockmate.batch.entity;


import com.stockmate.batch.entity.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "coin_price")
@IdClass(CoinPriceId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPrice extends BaseTimeEntity implements Persistable<CoinPriceId> {

    @Id
    private String symbol;

    @Id
    private long openTime; // 초단위로 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol")
    private Coin coin;

    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double volume; // 거래 코인의 양
    private double quoteAssetVolume; // 거래 코인의 가치(달러)
    private int numberOfTrades; // 거래 횟수

    @Override
    public CoinPriceId getId() {
        return new CoinPriceId(symbol, openTime);
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoinPrice)) {
            return false;
        }
        CoinPrice that = (CoinPrice) o;
        return openTime == that.openTime && symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, openTime);
    }
}
