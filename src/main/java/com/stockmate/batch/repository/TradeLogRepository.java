package com.stockmate.batch.repository;

import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.entity.TradeLogId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TradeLogRepository extends JpaRepository<TradeLog, TradeLogId>, TradeLogBulkRepository {

    @Query("select t from TradeLog t where t.id = :id and t.symbol = :symbol and t.isComplete = :isComplete")
    List<TradeLog> findAllByIdAndComplete(long id, String symbol, boolean isComplete);
}
