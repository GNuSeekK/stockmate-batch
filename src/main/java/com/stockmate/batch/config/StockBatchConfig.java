package com.stockmate.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class StockBatchConfig {

//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//    private final StockService stockService;
//    private final HankookTickDataManager hankookTickDataManager;
//
//    @Bean
//    public Job importStockJob() {
//        return new JobBuilder("importStockJob", jobRepository)
//            .incrementer(new RunIdIncrementer())
//            .start(stockDataFetchAndSaveStep())
//            .build();
//    }
//
//    @Bean
//    public Step stockDataFetchAndSaveStep() {
//        return new StepBuilder("stockDataFetchAndSaveStep", jobRepository)
//            .tasklet(stockDataFetchAndSaveTasklet(), transactionManager)
//            .allowStartIfComplete(true) // Step이 이미 완료된 경우에도 다시 실행되도록 설정
//            .build();
//    }
//
//    @Bean
//    public Tasklet stockDataFetchAndSaveTasklet() {
//        return (contribution, chunkContext) -> {
//            stockService.fetchAndSaveStockData(); // 데이터 저장
//            hankookTickDataManager.connect(); // WebSocket 연결
//            return RepeatStatus.FINISHED;
//        };
//    }
}
