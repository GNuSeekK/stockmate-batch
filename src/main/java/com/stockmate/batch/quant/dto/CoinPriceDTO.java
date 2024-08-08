package com.stockmate.batch.quant.dto;

import com.stockmate.batch.binance.dto.BinanceWebsocketDTO;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.entity.CoinPriceId;
import com.stockmate.batch.entity.CoinPriceSecond;
import com.stockmate.batch.util.BinanceUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoinPriceDTO {


    private CoinPriceId id;
    private long openTime; // 분단위로 저장
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double volume; // 거래 코인의 양
    private double quoteAssetVolume; // 거래 코인의 가치(달러)
    private int numberOfTrades; // 거래 횟수

    public static CoinPriceDTO of(CoinPrice coinPrice) {
        return CoinPriceDTO.builder()
            .id(coinPrice.getId())
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

    public static CoinPriceDTO of(CoinPriceSecond coinPriceSecond) {
        return CoinPriceDTO.builder()
            .id(coinPriceSecond.getId())
            .openTime(coinPriceSecond.getOpenTime())
            .openPrice(coinPriceSecond.getOpenPrice())
            .highPrice(coinPriceSecond.getHighPrice())
            .lowPrice(coinPriceSecond.getLowPrice())
            .closePrice(coinPriceSecond.getClosePrice())
            .volume(coinPriceSecond.getVolume())
            .quoteAssetVolume(coinPriceSecond.getQuoteAssetVolume())
            .numberOfTrades(coinPriceSecond.getNumberOfTrades())
            .build();
    }

    public CoinPriceDTO updatedDTO(CoinPriceSecond coinPriceSecond) {
        if (coinPriceSecond.getOpenTime() < this.openTime
            || coinPriceSecond.getOpenTime() >= this.openTime + BinanceUtil.ONE_MINUTE) {
            throw new IllegalArgumentException("시간이 맞지 않는 CoinPice를 업데이트 하려 시도했습니다");
        }
        return CoinPriceDTO.builder()
            .id(this.id)
            .openTime(this.openTime)
            .openPrice(this.openPrice)
            .highPrice(Math.max(this.highPrice, coinPriceSecond.getHighPrice()))
            .lowPrice(Math.min(this.lowPrice, coinPriceSecond.getLowPrice()))
            .closePrice(coinPriceSecond.getClosePrice())
            .volume(this.volume + coinPriceSecond.getVolume())
            .quoteAssetVolume(this.quoteAssetVolume + coinPriceSecond.getQuoteAssetVolume())
            .numberOfTrades(this.numberOfTrades + coinPriceSecond.getNumberOfTrades())
            .build();
    }

    public long getTime() {
        return openTime;
    }

    public double getAvgPrice() {
        return (highPrice + lowPrice) / 2;
    }


    public CoinPriceDTO makeNewUpdatedDTO(BinanceWebsocketDTO binanceWebsocketDTO) {
        return CoinPriceDTO.builder()
            .id(this.id)
            .openTime(this.openTime)
            .openPrice(this.openPrice)
            .highPrice(Math.max(this.highPrice, Double.parseDouble(binanceWebsocketDTO.getPrice())))
            .lowPrice(Math.min(this.lowPrice, Double.parseDouble(binanceWebsocketDTO.getPrice())))
            .closePrice(Double.parseDouble(binanceWebsocketDTO.getPrice()))
            .volume(this.volume + Double.parseDouble(binanceWebsocketDTO.getQuantity()))
            .quoteAssetVolume(
                this.quoteAssetVolume + Double.parseDouble(binanceWebsocketDTO.getPrice()) * Double.parseDouble(
                    binanceWebsocketDTO.getQuantity()))
            .numberOfTrades(this.numberOfTrades + 1)
            .build();
    }

    public static boolean isChangedValue(CoinPriceDTO before, CoinPriceDTO after) {
        return before.getHighPrice() != after.getHighPrice()
            || before.getLowPrice() != after.getLowPrice();
    }
}
