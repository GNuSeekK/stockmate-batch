package com.stockmate.batch.repository;

import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.entity.CoinPriceId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoinPriceRepository extends JpaRepository<CoinPrice, CoinPriceId>, CoinPriceBulkRepository {

    @Query("SELECT cp.openTime FROM CoinPrice cp WHERE cp.symbol = :symbol ORDER BY cp.openTime DESC LIMIT 1")
    Optional<Long> findLatestTimestamp(String symbol);

    @Query("SELECT cp FROM CoinPrice cp "
        + "WHERE cp.symbol = :symbol AND cp.openTime >= :startTime "
        + "ORDER BY cp.openTime ASC LIMIT :limit")
    List<CoinPrice> findCoinPricesWithLimitAndTime(String symbol, long startTime, int limit);
}
