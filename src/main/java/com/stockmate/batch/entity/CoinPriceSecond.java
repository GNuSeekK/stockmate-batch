package com.stockmate.batch.entity;

import com.stockmate.batch.entity.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "coin_price_second")
@IdClass(CoinPriceId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPriceSecond extends BaseTimeEntity implements Persistable<CoinPriceId> {

    @Id
    @Column(name = "coin_id")
    private long id;

    @Id
    private long openTime; // 분단위로 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", referencedColumnName = "coin_id", insertable = false, updatable = false)
    @MapsId("coin_id")
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
        return new CoinPriceId(id, openTime);
    }

    public long getRealId() {
        return id;
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
        if (!(o instanceof CoinPriceSecond that)) {
            return false;
        }
        return getId() == that.getId() && getOpenTime() == that.getOpenTime()
            && Double.compare(getOpenPrice(), that.getOpenPrice()) == 0
            && Double.compare(getHighPrice(), that.getHighPrice()) == 0
            && Double.compare(getLowPrice(), that.getLowPrice()) == 0
            && Double.compare(getClosePrice(), that.getClosePrice()) == 0
            && Double.compare(getVolume(), that.getVolume()) == 0
            && Double.compare(getQuoteAssetVolume(), that.getQuoteAssetVolume()) == 0
            && getNumberOfTrades() == that.getNumberOfTrades();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOpenTime(), getOpenPrice(), getHighPrice(), getLowPrice(), getClosePrice(),
            getVolume(), getQuoteAssetVolume(), getNumberOfTrades());
    }
}
