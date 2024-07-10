package com.stockmate.batch.service;

import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.repository.CoinPriceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    }

    public long getLatestTimestamp(String symbol) {
        return coinPriceRepository.findLatestTimestamp(symbol)
            .orElse(0L);
    }

    public List<CoinPrice> getCoinPrices(String symbol, long startTime, int limit) {
        return coinPriceRepository.findCoinPricesWithLimitAndTime(symbol, startTime, limit);
    }
}
