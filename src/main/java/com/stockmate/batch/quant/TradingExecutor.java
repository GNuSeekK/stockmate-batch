package com.stockmate.batch.quant;

import com.stockmate.batch.binance.dto.BinanceWebsocketDTO;
import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.quant.strategy.Strategy;
import com.stockmate.batch.quant.trader.Trader;
import com.stockmate.batch.service.AccountService;
import com.stockmate.batch.service.TradeLogService;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingExecutor {

    private final Trader trader;
    private final TradeLogService tradeLogService;
    private final AccountService accountService;
    @Getter
    private final Strategy strategy;
    private final List<TradeLog> remainedLogs = new ArrayList<>();

    @Getter
    private Account account;
    private CoinPriceDTO lastCoinPrice;

    public synchronized void trade(CoinPriceDTO coinPrice, boolean isLast) {
        lastCoinPrice = coinPrice;
        if (!strategy.updateNextData(coinPrice)) {
            log.info("아직 데이터가 부족하여 거래가 발생하지 않았습니다");
            return;
        }
        if (!isLast) {
            return;
        }
        Account old = account.copyForCheckChange();
        long time = coinPrice.getOpenTime();
        for (TradeLog tradeLog : remainedLogs) {
            if (strategy.shouldStopLoss(tradeLog, time) || strategy.shouldSellAll(tradeLog, time)) {
                trader.sellAll(account, tradeLog, coinPrice);
            } else if (strategy.shouldTakeProfit(tradeLog, time)) {
                trader.takeProfit(account, tradeLog, coinPrice, strategy.TAKE_PROFIT_PERCENT);
            }
        }
        if (strategy.shouldBuy(account, time)) {
            trader.buy(account, coinPrice, strategy.BUY_PERCENT).ifPresent(item -> {
                tradeLogService.save(item);
                remainedLogs.add(item);
            });
        }
        remainedLogs.stream().filter(TradeLog::isComplete).forEach(tradeLogService::save);
        remainedLogs.removeIf(TradeLog::isComplete);
        accountService.saveIfChanged(old, account);
    }

    public synchronized void trade(BinanceWebsocketDTO binanceWebsocketDTO) {
        CoinPriceDTO after = lastCoinPrice.makeNewUpdatedDTO(binanceWebsocketDTO);
        if (CoinPriceDTO.isChangedValue(lastCoinPrice, after)) {
            return;
        }
        trade(after, true);
    }

    public void initialSetAccount(Account account) {
        if (this.account != null) {
            throw new IllegalStateException("이미 계좌가 설정되었습니다");
        }
        this.account = account;
    }
}
