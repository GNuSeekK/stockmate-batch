package com.stockmate.batch.fmp.feign;

import com.stockmate.batch.fmp.feign.dto.PriceHistoryDto;
import com.stockmate.batch.fmp.feign.dto.StockSymbolDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fmp", url = "https://financialmodelingprep.com/api", configuration = FmpClientConfig.class)
public interface FmpClient {

    @GetMapping("/v3/available-traded/list")
    List<StockSymbolDto> getTradableStockSymbols();

    @GetMapping("/v3/financial-statement-symbol-lists")
    List<String> getFinancialStatementSymbolLists();

    @GetMapping("/v3/historical-price-full/{symbol}")
    PriceHistoryDto getStockPriceList(
        @PathVariable("symbol") String symbol,
        @RequestParam("from") LocalDate from,
        @RequestParam("to") LocalDate to);
}