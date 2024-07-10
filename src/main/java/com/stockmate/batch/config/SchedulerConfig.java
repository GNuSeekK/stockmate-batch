package com.stockmate.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final JobLauncher jobLauncher;
    //    private final Job importStockJob;
    private final Job importCoinPriceJob;

//    @Scheduled(cron = "0 0 8 * * ?")
//    public void saveStockData() throws Exception {
//        jobLauncher.run(importStockJob, new JobParametersBuilder().toJobParameters());
//    }

    private final JobExplorer jobExplorer;

    @Scheduled(cron = "*/10 * * * * *")
    public synchronized void saveCoinPriceData() throws Exception {
        jobLauncher.run(importCoinPriceJob, new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters());
    }

}

