package com.stockmate.batch.fmp.service;

import com.stockmate.batch.entity.StockPrice;
import com.stockmate.batch.fmp.feign.FmpClient;
import com.stockmate.batch.fmp.feign.dto.StockSymbolDto;
import com.stockmate.batch.repository.StockPriceRepository;
import com.stockmate.batch.repository.StockRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FmpService {

    private final FmpClient fmpClient;
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;

    @Transactional
    public void saveSymbolInfo() {
        Set<String> financialStatementSymbolLists = new HashSet<>(fmpClient.getFinancialStatementSymbolLists());
        List<StockSymbolDto> stockSymbolDtoList = fmpClient.getTradableStockSymbols()
            .stream()
            .filter(stockSymbolDto -> financialStatementSymbolLists.contains(stockSymbolDto.getSymbol()))
            .toList();
        stockRepository.saveAll(stockSymbolDtoList.parallelStream()
            .filter(StockSymbolDto::filterByExchange)
            .map(StockSymbolDto::toEntity)
            .toList());
    }

    @Transactional
    public void savePriceHistory(String symbol) {
        stockPriceRepository.findLatestStockPrice(symbol)
            .ifPresentOrElse(this::saveNextPrices, () -> saveAllPrices(symbol));
    }

    public void saveNextPrices(StockPrice stockPrice) {
        LocalDate endDay = LocalDate.now();
        LocalDate startDay = stockPrice.getId().getDate().plusDays(1);
        List<StockPrice> stockPriceList = getNextAllStockPrices(stockPrice.getId().getCode(), startDay, endDay);
        stockPriceRepository.saveAll(stockPriceList);
    }

    private List<StockPrice> getNextAllStockPrices(String symbol, LocalDate startDay, LocalDate endDay) {
        List<StockPrice> stockPriceList = new ArrayList<>();
        while (true) {
            LocalDate from = startDay;
            LocalDate to = getMaxToDate(startDay, endDay);
            stockPriceList.addAll(fmpClient.getStockPriceList(symbol, from, to)
                .toEntityList());
            if (to.equals(endDay)) {
                break;
            }
            startDay = to.plusDays(1);
        }
        return stockPriceList;
    }

    private LocalDate getMaxToDate(LocalDate startDay, LocalDate endDay) {
        // 5년치 데이터를 가져옴 (최대 5년치 데이터)
        int maxYear = 5;
        LocalDate to = startDay.plusYears(maxYear).minusDays(1);
        if (to.isAfter(endDay)) {
            to = endDay;
        }
        return to;
    }

    public void saveAllPrices(String symbol) {
        LocalDate endDay = LocalDate.now();
        LocalDate startDay = endDay.minusYears(30);
        List<StockPrice> stockPriceList = getNextAllStockPrices(symbol, startDay, endDay);
        stockPriceRepository.saveAll(stockPriceList);
    }

}
