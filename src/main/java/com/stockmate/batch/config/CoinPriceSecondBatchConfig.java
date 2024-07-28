package com.stockmate.batch.config;

import com.stockmate.batch.binance.dto.BinanceCoinPriceRequestDto;
import com.stockmate.batch.binance.service.BinanceFutureService;
import com.stockmate.batch.binance.service.BinanceService;
import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.entity.CoinPriceSecond;
import com.stockmate.batch.quant.TradingExecutor;
import com.stockmate.batch.repository.AccountRepository;
import com.stockmate.batch.service.CoinPriceSecondService;
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
public class CoinPriceSecondBatchConfig {

    private final JobRepository jobRepository;
    private final CoinPriceSecondService coinPriceService;
    private final BinanceService binanceService;
    private final PlatformTransactionManager transactionManager;
    private final CoinService coinService;
    private final JobExecutionListener listener;
    private final TradingExecutor tradingExecutor;
    private final AccountRepository accountRepository;
    private final BinanceFutureService binanceFutureService;

    @Bean
    public Job importCoinPriceSecondJob(JobExecutionListener listener) {
        return new JobBuilder("importCoinPriceSecondJob", jobRepository).incrementer(new RunIdIncrementer())
            .start(coinPriceSecondStep()).listener(listener).build();
    }

    @Bean
    public Step coinPriceSecondStep() {
        return new StepBuilder("coinPriceStep", jobRepository).<BinanceCoinPriceRequestDto, List<CoinPriceSecond>>chunk(
                1,
                transactionManager).reader(coinPriceSecondSymbolReader()).processor(coinPriceSecondProcessor())
            .writer(coinPriceSecondWriter())
            .build();
    }


    @Bean
    @StepScope
    public ItemReader<BinanceCoinPriceRequestDto> coinPriceSecondSymbolReader() {
        return new ItemReader<BinanceCoinPriceRequestDto>() {
            private List<BinanceCoinPriceRequestDto> requests;
            private int index = 0;

            @Override
            public BinanceCoinPriceRequestDto read() {
                if (requests == null) {
                    // getKind가 "F가 아닌 것들만"
                    List<Coin> symbols = coinService.getCoinSymbols().stream()
                        .filter(item -> !item.getKind().equals("F"))
                        .toList();
                    this.requests = binanceService.createCoinPriceRequestDtos(symbols, "1s");
                }
                if (index < requests.size()) {
                    return requests.get(index++);
                }
                return null;
            }
        };
    }

    @Bean
    public ItemProcessor<BinanceCoinPriceRequestDto, List<CoinPriceSecond>> coinPriceSecondProcessor() {
        return item -> {
            List<CoinPrice> coinPrices = binanceService.getCoinPrice(item);
            return coinPrices.stream().map(CoinPrice::toSecond).toList();
        };
    }


    @Bean
    public ItemWriter<List<CoinPriceSecond>> coinPriceSecondWriter() {
        return items -> {
//            Account account = accountRepository.findLastestAccount();
            for (List<CoinPriceSecond> item : items) {
                coinPriceService.saveAllWithBulk(item);
//                for (int i = 0; i < item.size(); i++) {
//                    CoinPriceSecond coinPrice = item.get(i);
//                    synchronized (tradingExecutor) {
//                        tradingExecutor.trade(account, CoinPriceDTO.of(coinPrice), i == item.size() - 1);
//                    }
//                }
            }
//            accountRepository.save(account);
        };
    }

}
