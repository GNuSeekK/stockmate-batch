package com.stockmate.batch.quant;

import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.quant.strategy.Strategy;
import com.stockmate.batch.quant.trader.FakeTrader;
import com.stockmate.batch.quant.trader.Trader;
import com.stockmate.batch.service.TradeLogService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Scope(value = "prototype")
public class TestTradingExecutor {

    private final Trader trader;
    private final TradeLogService tradeLogService;
    @Getter
    private final Strategy strategy;

    public List<TradeLog> trade(List<TradeLog> remainedLogs, Account account, CoinPriceDTO coinPrice, boolean isLast) {
        List<TradeLog> completedLogs = new ArrayList<>();
        if (!strategy.updateNextData(coinPrice)) {
            return completedLogs;
        }
        if (!isLast) {
            return completedLogs;
        }
        long time = coinPrice.getOpenTime();
        for (TradeLog tradeLog : remainedLogs) {
            if (strategy.shouldStopLoss(tradeLog, time) || strategy.shouldSellAll(tradeLog, time)) {
                trader.sellAll(account, tradeLog, coinPrice);
            } else if (strategy.shouldTakeProfit(tradeLog, time)) {
                trader.takeProfit(account, tradeLog, coinPrice, strategy.TAKE_PROFIT_PERCENT);
            }
        }
        if (strategy.shouldBuy(account, time)) {
            Instant instant = Instant.ofEpochMilli(time);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            trader.buy(account, coinPrice, strategy.BUY_PERCENT).ifPresent(item -> {
                item.updateBuyDateTime(dateTime);
                remainedLogs.add(item);
            });
        }
//        remainedLogs.stream().filter(TradeLog::isComplete).forEach(tradeLogService::save);
        remainedLogs.stream().filter(TradeLog::isComplete).forEach(completedLogs::add);
        remainedLogs.removeIf(TradeLog::isComplete);
        return completedLogs;
    }

    public boolean isFakeTrader() {
        return trader instanceof FakeTrader;
    }
}
