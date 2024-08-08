package com.stockmate.batch.quant.common;

import com.stockmate.batch.quant.dto.CoinPriceDTO;

public interface CommonRepository {

//    void updateNextData(long time);

    boolean updateNextData(CoinPriceDTO coinPrice);

    void init();

    int getNeedDataSize();
}
