package com.stockmate.batch.repository;


import com.stockmate.batch.entity.StockTick;
import com.stockmate.batch.entity.StockTickId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTickRepository extends JpaRepository<StockTick, StockTickId> {

}
