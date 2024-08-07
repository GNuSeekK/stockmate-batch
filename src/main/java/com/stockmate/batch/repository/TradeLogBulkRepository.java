package com.stockmate.batch.repository;

import com.stockmate.batch.entity.TradeLog;
import java.util.List;

public interface TradeLogBulkRepository {

    void saveAllWithBulk(List<TradeLog> completedLogs);

}
