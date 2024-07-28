package com.stockmate.batch.service;

import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.repository.CoinPriceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CoinPriceService {

    private final CoinPriceRepository coinPriceRepository;

    @Transactional
    public void saveAllWithBulk(List<CoinPrice> coinPrices) {
        coinPriceRepository.bulkSaveAllCoinPrice(coinPrices);
        log.info("CoinPrice bulk save success");
    }

    public long getLatestTimestamp(Coin coin) {
        return coinPriceRepository.findLatestTimestamp(coin.getSymbol())
            .orElse(0L);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<CoinPrice> getCoinPrices(String symbol, long startTime, long endTime, String kind) {
        return coinPriceRepository.findCoinPricesWithTimes(symbol, startTime, endTime, kind);
    }

    public List<CoinPrice> getLatestCoinPrices(String symbol, int limit) {
        return coinPriceRepository.findLatestCoinPrices(symbol, limit);
    }
}
