package com.stockmate.batch.repository;

import com.stockmate.batch.entity.Coin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, Long> {


    Optional<Coin> findBySymbol(String symbol);
}
