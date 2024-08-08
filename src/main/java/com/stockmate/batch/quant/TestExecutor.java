package com.stockmate.batch.quant;

import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.entity.CoinPriceSecond;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.common.CommonRepository;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.quant.strategy.Strategy;
import com.stockmate.batch.quant.trader.FakeTrader;
import com.stockmate.batch.service.AccountService;
import com.stockmate.batch.service.CoinPriceSecondService;
import com.stockmate.batch.service.CoinPriceService;
import com.stockmate.batch.service.CoinService;
import com.stockmate.batch.service.TradeLogService;
import com.stockmate.batch.util.BinanceUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Scope(value = "prototype")
public class TestExecutor {

    private final EntityManager em;


    private final FakeTrader fakeTrader;
    private final AccountService accountService;
    private final TradeLogService tradeLogService;
    private final CoinPriceService coinPriceService;
    private final CoinPriceSecondService coinPriceSecondService;
    private final CoinService coinService;
    private final TestTradingExecutor tradingExecutor;

    // 21년 상승장
    // 22년 하락장
    // 23년 횡보 + 약간 상승
    // 24년 상승장
    @Async
    public CompletableFuture<Void> execute(String accountNo, Integer year, Integer month, String accountMemo,
        List<CoinPriceSecond> coinPriceSeconds) {
        return CompletableFuture.runAsync(() -> {
            checkIfTest();
            log.info("accountNo : {}, year : {}, month : {}, accountMemo : {}", accountNo, year, month, accountMemo);

            long start = BinanceUtil.getTime(year, month, 1, 0, 0);
            if (coinPriceSeconds.isEmpty() || coinPriceSeconds.get(0).getOpenTime() != start) {
                log.info("백테스트 데이터가 없거나 부족합니다");
                return;
            }
            initialDataSet(start);

            // 계좌 생성
            // 계좌에 번호 부여
            // 계좌에 메모 부여
            double asset = 10000.0;
            Account account = makeNewAccount(accountNo, year, month, accountMemo, asset);

            Coin coin = coinService.getCoinBySymbolAndKind(BinanceUtil.COIN, "P");
            // 초기 데이터 로그 셋팅
            List<TradeLog> remainedLogs = new ArrayList<>();
            List<TradeLog> completedLogs = new ArrayList<>();
            account = accountService.save(account);

            CoinPriceDTO lastDTO = doTradeRequest(coinPriceSeconds, completedLogs, remainedLogs, account);

            sellRemainedLogs(remainedLogs, account, lastDTO, completedLogs);
            saveEntities(account, completedLogs);
        });
    }

    private void saveEntities(Account account, List<TradeLog> completedLogs) {
        account.calculateProfit();
        accountService.save(account);
        tradeLogService.saveAllWithBulk(completedLogs);
    }

    private void sellRemainedLogs(List<TradeLog> remainedLogs, Account account, CoinPriceDTO lastDTO,
        List<TradeLog> completedLogs) {
        for (TradeLog tradeLog : remainedLogs) {
            fakeTrader.sellAll(account, tradeLog, lastDTO);
            completedLogs.add(tradeLog);
        }
    }

    private CoinPriceDTO doTradeRequest(List<CoinPriceSecond> coinPriceSeconds, List<TradeLog> completedLogs,
        List<TradeLog> remainedLogs, Account account) {
        CoinPriceDTO request = CoinPriceDTO.of(coinPriceSeconds.get(0));
        for (CoinPriceSecond coinPrice : coinPriceSeconds) {
            if (coinPrice.getOpenTime() >= request.getOpenTime() + BinanceUtil.ONE_MINUTE) {
                request = CoinPriceDTO.of(coinPrice);
            }
            request = request.updatedDTO(coinPrice);
            completedLogs.addAll(tradingExecutor.trade(remainedLogs, account, request, true));
        }
        return request;
    }

    private Account makeNewAccount(String accountNo, Integer year, Integer month, String accountMemo, double asset) {
        return Account.builder()
            .accountNo(accountNo)
            .memo(accountMemo)
            .name(BinanceUtil.COIN)
            .totalAsset(asset)
            .cash(asset)
            .unit("USDT")
            .startAsset(asset)
            .startDate(LocalDateTime.of(year, month, 1, 0, 0))
            .build();
    }

    private void initialDataSet(long start) {
        Strategy strategy = tradingExecutor.getStrategy();
        strategy.getCommonRepositories().forEach(CommonRepository::init); // 초기화
        Integer dataNum = strategy.getCommonRepositories()
            .stream().map(CommonRepository::getNeedDataSize).max(Integer::compareTo).orElseThrow();
        long prevStartTime = start - dataNum * BinanceUtil.ONE_MINUTE;
        coinPriceService.getCoinPrices(BinanceUtil.COIN, prevStartTime, start, "P")
            .forEach(coinPrice -> strategy.updateNextData(CoinPriceDTO.of(coinPrice)));
    }

    private void checkIfTest() {
        if (!tradingExecutor.isFakeTrader()) {
            throw new RuntimeException("테스트용으로만 사용 가능한 메서드입니다.");
        }
    }


    public Strategy getStrategy() {
        return tradingExecutor.getStrategy();
    }

}
