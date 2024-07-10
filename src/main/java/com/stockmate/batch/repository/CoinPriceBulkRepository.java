package com.stockmate.batch.repository;

import com.stockmate.batch.entity.CoinPrice;
import java.util.List;

public interface CoinPriceBulkRepository {

    void bulkSaveAllCoinPrice(List<CoinPrice> coinPrices);

}
