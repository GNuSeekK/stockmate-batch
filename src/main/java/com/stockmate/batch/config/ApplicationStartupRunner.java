package com.stockmate.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupRunner {

    private final JobLauncher jobLauncher;
    private final Job importStockJob;
    private final Job importCoinPriceJob;

    @Bean
    public ApplicationRunner run() {
        return args -> {
//            jobLauncher.run(importStockJob, new JobParametersBuilder().toJobParameters());
//            LocalDateTime now = LocalDateTime.now();
//            log.info("Job started at {}", now);
//            jobLauncher.run(importCoinPriceJob,
//                new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
//            log.info("Job finished at {}", LocalDateTime.now());
//            log.info("Job execution time: {} seconds",
//                (System.currentTimeMillis() - now.toLocalTime().toSecondOfDay()) / 1000);
        };
    }
}
