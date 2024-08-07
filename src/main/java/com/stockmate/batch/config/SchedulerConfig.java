package com.stockmate.batch.config;

import com.stockmate.batch.binance.websocket.BinanceTickDataManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Profile("real")
public class SchedulerConfig {

    private final JobLauncher jobLauncher;
    //    private final Job importStockJob;
//    private final Job importCoinPriceJob;
    private final Job importCoinPriceRealJob;
    private final BinanceTickDataManager binanceTickDataManager;

//    @Scheduled(cron = "0 0 8 * * ?")
//    public void saveStockData() throws Exception {
//        jobLauncher.run(importStockJob, new JobParametersBuilder().toJobParameters());
//    }

    private final JobExplorer jobExplorer;

    @Scheduled(cron = "*/1 * * * * *")
    public synchronized void saveCoinPriceData() throws Exception {
        jobLauncher.run(importCoinPriceRealJob, new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters());
    }

    // 12시간에 한번
    @Scheduled(cron = "0 0/5 0 * * ?")
    public synchronized void reconnectBinanceCoinSocket() {
        binanceTickDataManager.connect();
    }

}

