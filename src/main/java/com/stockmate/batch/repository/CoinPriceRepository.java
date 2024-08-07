package com.stockmate.batch.repository;

import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.entity.CoinPriceId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoinPriceRepository extends JpaRepository<CoinPrice, CoinPriceId>, CoinPriceBulkRepository {

    @Query("SELECT cp.openTime FROM CoinPrice cp WHERE cp.coin = :coin ORDER BY cp.openTime DESC LIMIT 1")
    Optional<Long> findLatestTimestamp(Coin coin);

    @Query("SELECT cp FROM CoinPrice cp "
        + "WHERE cp.coin.symbol = :symbol "
        + "ORDER BY cp.openTime DESC LIMIT :limit")
    List<CoinPrice> findLatestCoinPrices(String symbol, int limit);

    @Query("SELECT cp FROM CoinPrice cp "
        + "WHERE cp.coin.symbol = :symbol "
        + "AND cp.coin.kind = :kind "
        + "AND cp.openTime >= :startTime "
        + "AND cp.openTime < :endTime "
        + "ORDER BY cp.openTime ASC")
    List<CoinPrice> findCoinPricesWithTimes(String symbol, long startTime, long endTime, String kind);

}
