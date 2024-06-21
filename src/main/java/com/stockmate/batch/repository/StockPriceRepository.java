package com.stockmate.batch.repository;

import com.stockmate.batch.entity.StockDateId;
import com.stockmate.batch.entity.StockPrice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockPriceRepository extends JpaRepository<StockPrice, StockDateId> {

    @Query("SELECT sp FROM StockPrice sp WHERE sp.id.code = :code ORDER BY sp.id.date DESC")
    Optional<StockPrice> findLatestStockPrice(String code);

}
