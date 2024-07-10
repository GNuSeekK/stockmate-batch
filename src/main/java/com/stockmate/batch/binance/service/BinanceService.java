package com.stockmate.batch.binance.service;

import com.stockmate.batch.binance.dto.BinanceCoinPriceRequestDto;
import com.stockmate.batch.entity.CoinPrice;
import java.util.List;

public interface BinanceService {

    List<CoinPrice> getCoinPrice(BinanceCoinPriceRequestDto request);

    List<BinanceCoinPriceRequestDto> createCoinPriceRequestDtos(List<String> symbols);

}
