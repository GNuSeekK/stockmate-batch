package com.stockmate.batch.config;

import com.stockmate.batch.slack.SlackService;
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
@Profile("crawling")
public class ApplicationCrawlingStartupRunner {

    private final SlackService slackService;
    private final Job importCoinPriceSecondJob;
    private final Job importCoinPriceJob;
    private final JobLauncher jobLauncher;

    @Bean
    public ApplicationRunner run() {
        return args -> {
            jobLauncher.run(importCoinPriceJob,
                new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
            slackService.sendMessage("백테스트용 분 데이터 수집이 완료되었습니다.");
            jobLauncher.run(importCoinPriceSecondJob,
                new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
            slackService.sendMessage("백테스트용 초 데이터 수집이 완료되었습니다.");
        };
    }
}
