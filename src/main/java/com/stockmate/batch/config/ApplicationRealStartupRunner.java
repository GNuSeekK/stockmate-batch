package com.stockmate.batch.config;

import com.stockmate.batch.binance.feign.dto.AccountInfo;
import com.stockmate.batch.binance.service.BinanceFutureService;
import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.quant.TestExecutor;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.quant.strategy.Strategy;
import com.stockmate.batch.repository.AccountRepository;
import com.stockmate.batch.service.CoinPriceService;
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

    private final JobLauncher jobLauncher;
    //    private final Job importStockJob;
    private final Job importCoinPriceJob;
    private final TestExecutor testExecutor;
    private final Strategy strategy;
    private final CoinPriceService coinPriceService;
    private final AccountRepository accountRepository;
    private final BinanceFutureService binanceFutureService;

    @Bean
    public ApplicationRunner run() {
        return args -> {
            synchronized (strategy) {
                AccountInfo info = binanceFutureService.getAccountInfo();
                Account account = info.toEntity();
                accountRepository.save(account);
                List<CoinPrice> latestCoinPrices = coinPriceService.getLatestCoinPrices(BinanceUtil.COIN, 50);
                for (int i = latestCoinPrices.size() - 1; i >= 0; i--) {
                    CoinPrice coinPrice = latestCoinPrices.get(i);
                    strategy.updateNextData(CoinPriceDTO.of(coinPrice));
                }
            }
        };
    }
}
