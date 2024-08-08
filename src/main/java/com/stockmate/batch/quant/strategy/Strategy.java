package com.stockmate.batch.quant.strategy;

import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.common.CommonRepository;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.quant.helper.TestHelper;
import com.stockmate.batch.repository.AccountLogRepository;
import com.stockmate.batch.repository.TradeLogRepository;
import com.stockmate.batch.service.AccountService;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class Strategy {

    public Double BUY_PERCENT = 0.3;
    public Double TAKE_PROFIT_PERCENT = 0.3;

    private final AccountService accountService;
    private final AccountLogRepository accountLogRepository;
    private final TradeLogRepository tradeLogRepository;
    @Getter
    private List<CommonRepository> commonRepositories = new ArrayList<>();

    void addCommonRepositories(CommonRepository commonRepositories) {
        this.commonRepositories.add(commonRepositories);
    }

    void resetCommonRepositories() {
        this.commonRepositories = new ArrayList<>();
    }

    public abstract boolean shouldBuy(Account account, long time);

    public abstract boolean shouldSellAll(TradeLog log, long time);

    public abstract boolean shouldTakeProfit(TradeLog log, long time);

    public abstract boolean shouldStopLoss(TradeLog log, long time);

    public boolean updateNextData(CoinPriceDTO coinPrice) {
        boolean result = true;
        for (CommonRepository commonRepository : commonRepositories) {
            result = commonRepository.updateNextData(coinPrice) && result;
        }
        return result;
    }

    public abstract String getName();

    public void changeCalcDatas(TestHelper testHelper, double buyPercent, double takeProfitPercent) {
        BUY_PERCENT = buyPercent;
        TAKE_PROFIT_PERCENT = takeProfitPercent;
    }

    public abstract void changeCalcDatas(Object o);

}
