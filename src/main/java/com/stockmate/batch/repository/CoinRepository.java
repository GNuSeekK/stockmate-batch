package com.stockmate.batch.repository;

import com.stockmate.batch.entity.Coin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoinRepository extends JpaRepository<Coin, Long> {


    Optional<Coin> findBySymbol(String symbol);

    @Query("select c from Coin c where c.symbol = :symbol and c.kind = :kind")
    Optional<Coin> findBySymbolAndKind(String symbol, String kind);
}
