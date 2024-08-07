package com.stockmate.batch.config;

import com.stockmate.batch.binance.feign.dto.AccountInfo;
import com.stockmate.batch.binance.service.BinanceFutureService;
import com.stockmate.batch.binance.websocket.BinanceTickDataManager;
import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.quant.TradingExecutor;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.quant.strategy.Strategy;
import com.stockmate.batch.repository.AccountRepository;
import com.stockmate.batch.service.CoinPriceService;
import com.stockmate.batch.slack.SlackService;
import com.stockmate.batch.util.BinanceUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("real")
public class ApplicationRealStartupRunner {

    private final TradingExecutor tradingExecutor;
    private final CoinPriceService coinPriceService;
    private final AccountRepository accountRepository;
    private final BinanceFutureService binanceFutureService;
    private final BinanceTickDataManager binanceTickDataManager;
    private final Job importCoinPriceRealJob;
    private final JobLauncher jobLauncher;
    private final SlackService slackService;


    @Bean
    public ApplicationRunner run() {
        return args -> {
            slackService.sendMessage(tradingExecutor.getStrategy().getName() + " - 자동 매매를 시작합니다.");
            synchronized (tradingExecutor) {
                AccountInfo info = binanceFutureService.getAccountInfo();
                Account account = info.toEntity();
                Account savedAccount = accountRepository.save(account);
                slackService.sendMessage(
                    "초기 계좌 현금 - " + savedAccount.getCash() + "\n초기 계좌 자산 - " + savedAccount.getStartAsset());
                tradingExecutor.initialSetAccount(savedAccount);
                Strategy strategy = tradingExecutor.getStrategy();
                List<CoinPrice> latestCoinPrices = coinPriceService.getLatestCoinPrices(BinanceUtil.COIN, 365);
                for (int i = latestCoinPrices.size() - 1; i >= 0; i--) {
                    CoinPrice coinPrice = latestCoinPrices.get(i);
                    strategy.updateNextData(CoinPriceDTO.of(coinPrice));
                }
//                jobLauncher.run(importCoinPriceRealJob,
//                    new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
//                slackService.sendMessage("코인 가격 데이터 최초 업데이트를 완료하였습니다.");
            }
            slackService.sendMessage("가격 데이터 초기화 완료, 트레이딩 감시를 시작합니다.");
            binanceTickDataManager.connect();
        };
    }
}
