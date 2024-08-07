package com.stockmate.batch.config;

import com.stockmate.batch.binance.dto.BinanceCoinPriceRequestDto;
import com.stockmate.batch.binance.service.BinanceFutureService;
import com.stockmate.batch.binance.service.BinanceService;
import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.quant.TradingExecutor;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.service.CoinPriceService;
import com.stockmate.batch.service.CoinService;
import com.stockmate.batch.util.BinanceUtil;
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
public class CoinPriceRealBatchConfig {

    private final JobRepository jobRepository;
    private final CoinPriceService coinPriceService;
    private final BinanceService binanceService;
    private final PlatformTransactionManager transactionManager;
    private final CoinService coinService;
    private final JobExecutionListener listener;
    private final TradingExecutor tradingExecutor;
    private final BinanceFutureService binanceFutureService;

    @Bean
    public Job importCoinPriceRealJob(JobExecutionListener listener) {
        return new JobBuilder("importCoinPriceRealJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(coinPriceRealStep())
            .listener(listener)
            .build();
    }

    @Bean
    public Step coinPriceRealStep() {
        return new StepBuilder("coinPriceStep", jobRepository)
            .<BinanceCoinPriceRequestDto, List<CoinPrice>>chunk(1, transactionManager)
            .reader(coinSymbolRealReader())
            .processor(coinPriceRealProcessor())
            .writer(coinPriceRealWriter())
            .build();
    }


    @Bean
    @StepScope
    public ItemReader<BinanceCoinPriceRequestDto> coinSymbolRealReader() {
        return new ItemReader<BinanceCoinPriceRequestDto>() {
            private List<BinanceCoinPriceRequestDto> requests;
            private int index = 0;

            @Override
            public BinanceCoinPriceRequestDto read() {
                if (requests == null) {
                    Coin coin = coinService.getCoinBySymbolAndKind(BinanceUtil.COIN, "F");
                    this.requests = binanceService.createCoinPriceRequestDtos(coin, "1m");
                }
                if (index < requests.size()) {
                    return requests.get(index++);
                }
                return null;
            }
        };
    }

    @Bean
    public ItemProcessor<BinanceCoinPriceRequestDto, List<CoinPrice>> coinPriceRealProcessor() {
        return binanceService::getCoinPrice;
    }


    @Bean
    public ItemWriter<List<CoinPrice>> coinPriceRealWriter() {
        return items -> {
            for (List<CoinPrice> item : items) {
                synchronized (tradingExecutor) {
                    if (item.size() > 1) { // 1개 초과일때만 저장 및 트레이딩 실제로 시행함. 아니면 굳이 할필요 없음 websocket이 해주기 때문
                        coinPriceService.saveAllWithBulk(item);
                        for (int i = 0; i < item.size(); i++) {
                            CoinPrice coinPrice = item.get(i);
                            tradingExecutor.trade(CoinPriceDTO.of(coinPrice), i == item.size() - 1);
                        }
                    }
                }
            }
        };
    }

}
