package com.stockmate.batch.service;

import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.entity.CoinPriceSecond;
import com.stockmate.batch.repository.CoinPriceSecondRepository;
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
public class CoinPriceSecondService {

    private final CoinPriceSecondRepository coinPriceSecondRepository;

    @Transactional
    public void saveAllWithBulk(List<CoinPriceSecond> coinPrices) {
        coinPriceSecondRepository.bulkSaveAllCoinPrice(coinPrices);
        log.info("CoinPrice bulk save success");
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<CoinPriceSecond> getPresentCoinPrices(Coin coin, long startTime, long endTime) {
        return coinPriceSecondRepository.findPresentCoinPricesWithTimes(coin, startTime, endTime);
    }

}
