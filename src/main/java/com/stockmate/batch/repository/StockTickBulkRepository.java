package com.stockmate.batch.repository;

import com.stockmate.batch.entity.StockTick;
import java.util.List;

public interface StockTickBulkRepository {

    void bulkSaveAllTick(List<StockTick> stockTicks);

}
