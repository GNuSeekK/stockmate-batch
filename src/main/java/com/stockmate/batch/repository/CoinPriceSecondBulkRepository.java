package com.stockmate.batch.repository;

import com.stockmate.batch.entity.CoinPriceSecond;
import java.util.List;

public interface CoinPriceSecondBulkRepository {

    void bulkSaveAllCoinPrice(List<CoinPriceSecond> coinPrices);

}
