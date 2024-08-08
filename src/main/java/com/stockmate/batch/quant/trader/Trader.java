package com.stockmate.batch.quant.trader;

import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import java.util.Optional;

public interface Trader {

    Optional<TradeLog> buy(Account account, CoinPriceDTO coinPrice, double buyPercent);

    TradeLog sellAll(Account account, TradeLog tradeLog, CoinPriceDTO coinPrice);

    TradeLog takeProfit(Account account, TradeLog tradeLog, CoinPriceDTO coinPrice, double takeProfitPercent);
}
