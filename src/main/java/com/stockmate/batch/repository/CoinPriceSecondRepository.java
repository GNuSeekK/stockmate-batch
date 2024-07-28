package com.stockmate.batch.repository;

import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.entity.CoinPriceId;
import com.stockmate.batch.entity.CoinPriceSecond;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoinPriceSecondRepository extends JpaRepository<CoinPriceSecond, CoinPriceId>,
    CoinPriceSecondBulkRepository {

    @Query("SELECT cp.openTime FROM CoinPriceSecond cp WHERE cp.coin.symbol = :symbol ORDER BY cp.openTime DESC LIMIT 1")
    Optional<Long> findLatestTimestamp(String symbol);

    @Query("SELECT cp FROM CoinPriceSecond cp "
        + "WHERE cp.coin = :coin "
        + "AND cp.openTime >= :startTime "
        + "AND cp.openTime < :endTime ")
    List<CoinPriceSecond> findPresentCoinPricesWithTimes(Coin coin, long startTime, long endTime);
}
