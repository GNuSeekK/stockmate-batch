package com.stockmate.batch.config;

import com.stockmate.batch.binance.dto.BinanceCoinPriceRequestDto;
import com.stockmate.batch.binance.service.BinanceService;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.service.CoinPriceService;
import com.stockmate.batch.service.CoinService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class CoinBatchConfig {

    private final JobRepository jobRepository;
    private final CoinPriceService coinPriceService;
    private final BinanceService binanceService;
    private final PlatformTransactionManager transactionManager;
    private final CoinService coinService;
    private final JobExecutionListener listener;

    @Bean
    public Job importCoinPriceJob(JobExecutionListener listener) {
        return new JobBuilder("importCoinPriceJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(coinPriceStep())
            .listener(listener)
            .build();
    }

    @Bean
    public Step coinPriceStep() {
        return new StepBuilder("coinPriceStep", jobRepository)
            .<BinanceCoinPriceRequestDto, List<CoinPrice>>chunk(1, transactionManager)
            .reader(coinSymbolReader())
            .processor(coinPriceProcessor())
            .writer(coinPriceWriter())
            .build();
    }


    @Bean
    @StepScope
    public ItemReader<BinanceCoinPriceRequestDto> coinSymbolReader() {
        return new ItemReader<BinanceCoinPriceRequestDto>() {
            private List<BinanceCoinPriceRequestDto> requests;
            private int index = 0;

            @Override
            public BinanceCoinPriceRequestDto read() {
                if (requests == null) {
                    List<String> symbols = coinService.getCoinSymbols();
                    this.requests = binanceService.createCoinPriceRequestDtos(symbols);
                }
                if (index < requests.size()) {
                    return requests.get(index++);
                }
                return null;
            }
        };
    }

    @Bean
    public ItemProcessor<BinanceCoinPriceRequestDto, List<CoinPrice>> coinPriceProcessor() {
        return binanceService::getCoinPrice;
    }


    @Bean
    public ItemWriter<List<CoinPrice>> coinPriceWriter() {
        return items -> {
            for (List<CoinPrice> item : items) {
                coinPriceService.saveAllWithBulk(item);
            }
        };
    }

}
