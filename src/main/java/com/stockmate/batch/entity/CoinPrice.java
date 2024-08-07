package com.stockmate.batch.entity;


import com.stockmate.batch.binance.dto.BinanceWebsocketDTO;
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

    public static CoinPriceSecond toSecond(CoinPrice coinPrice) {
        return CoinPriceSecond.builder()
            .id(coinPrice.getId().getId())
            .openTime(coinPrice.getOpenTime())
            .openPrice(coinPrice.getOpenPrice())
            .highPrice(coinPrice.getHighPrice())
            .lowPrice(coinPrice.getLowPrice())
            .closePrice(coinPrice.getClosePrice())
            .volume(coinPrice.getVolume())
            .quoteAssetVolume(coinPrice.getQuoteAssetVolume())
            .numberOfTrades(coinPrice.getNumberOfTrades())
            .build();
    }

    public void update(BinanceWebsocketDTO binanceWebsocketDTO) {
        this.closePrice = Double.parseDouble(binanceWebsocketDTO.getPrice());
        if (this.highPrice < this.closePrice) {
            this.highPrice = this.closePrice;
        }
        if (this.lowPrice > this.closePrice) {
            this.lowPrice = this.closePrice;
        }
    }
}
