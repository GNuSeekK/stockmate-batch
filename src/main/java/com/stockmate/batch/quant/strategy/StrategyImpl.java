package com.stockmate.batch.quant.strategy;

import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.repository.AccountLogRepository;
import com.stockmate.batch.repository.TradeLogRepository;
import com.stockmate.batch.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(value = "prototype")
@Component
@Profile("test || crawling")
@Slf4j
public class StrategyImpl extends Strategy {

    public StrategyImpl(AccountService accountService,
        AccountLogRepository accountLogRepository,
        TradeLogRepository tradeLogRepository) {
        super(accountService, accountLogRepository, tradeLogRepository);
    }

    @Override
    public boolean shouldBuy(Account account, long time) {
        return false;
    }

    @Override
    public boolean shouldSellAll(TradeLog log, long time) {
        return false;
    }

    @Override
    public boolean shouldTakeProfit(TradeLog log, long time) {
        return false;
    }

    @Override
    public boolean shouldStopLoss(TradeLog log, long time) {
        return false;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void changeCalcDatas(Object o) {

    }
}
