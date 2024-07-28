package com.stockmate.batch.config;

import com.stockmate.batch.binance.service.BinanceFutureService;
import com.stockmate.batch.quant.helper.TestHelper;
import com.stockmate.batch.quant.strategy.Strategy;
import com.stockmate.batch.repository.AccountRepository;
import com.stockmate.batch.service.CoinPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!real")
public class ApplicationTestStartupRunner {

    private final JobLauncher jobLauncher;
    //    private final Job importStockJob;
    private final Job importCoinPriceJob;
    private final Job importCoinPriceSecondJob;
    private final TestHelper testHelper;
    private final Strategy strategy;
    private final CoinPriceService coinPriceService;
    private final AccountRepository accountRepository;
    private final BinanceFutureService binanceFutureService;

    @Bean
    public ApplicationRunner run() {
        return args -> {
//            LocalDateTime now = LocalDateTime.now();
//            log.info("Job started at {}", now);
//            jobLauncher.run(importCoinPriceSecondJob,
//                new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
//            jobLauncher.run(importCoinPriceJob,
//                new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
//            log.info("Job finished at {}", LocalDateTime.now());
//            log.info("Job execution time: {} seconds",
//                (System.currentTimeMillis() - now.toLocalTime().toSecondOfDay()) / 1000);
            testHelper.doTest();
        };
    }
}
