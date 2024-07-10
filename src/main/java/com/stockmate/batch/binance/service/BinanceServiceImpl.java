package com.stockmate.batch.binance.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmate.batch.binance.dto.BinanceCoinPriceDTO;
import com.stockmate.batch.binance.dto.BinanceCoinPriceRequestDto;
import com.stockmate.batch.entity.CoinPrice;
import com.stockmate.batch.service.CoinPriceService;
import com.stockmate.batch.util.BinanceUtil;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class BinanceServiceImpl implements BinanceService {

    private final CoinPriceService coinPriceService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String INTERVAL = "1m"; // 인터벌 설정
    private static final int MULTIPLE = 60; // 몇초
    private static final int TIME_MULTIPLE = 1000; // 시간 단위 맞추기 위한 값
    private static final int LIMIT = 1000; // 몇개씩

    @Autowired
    public BinanceServiceImpl(CoinPriceService coinPriceService) {
        this.coinPriceService = coinPriceService;
    }


    @Override
    public List<CoinPrice> getCoinPrice(BinanceCoinPriceRequestDto request) {
        String url = "https://api.binance.com/api/v3/uiKlines?symbol=" + request.getSymbol()
            + "&interval=" + INTERVAL + "&startTime=" + request.getStartTime() + "&endTime=" + request.getEndTime()
            + "&limit=" + LIMIT;
        String response = restTemplate.getForObject(url, String.class);
        try {
            List<BinanceCoinPriceDTO> coinPriceDTOs = objectMapper.readValue(response,
                new TypeReference<List<BinanceCoinPriceDTO>>() {
                });
            return coinPriceDTOs.stream()
                .map(dto -> dto.toEntity(request.getSymbol()))
                .toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get new coin prices from Binance API");
        }
    }

    @Override
    public List<BinanceCoinPriceRequestDto> createCoinPriceRequestDtos(List<String> symbols) {
        List<BinanceCoinPriceRequestDto> requests = new ArrayList<>(List.of());
        for (String symbol : symbols) {
            long startTime = coinPriceService.getLatestTimestamp(symbol);
            if (startTime == 0) {
                startTime = getStartTimeFromBinanceApi(symbol);
            }
            long endTime = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) * TIME_MULTIPLE;
            // startTime 부터 endTime까지 1000초씩 나눠서 넣기
            long unit = TIME_MULTIPLE * LIMIT * MULTIPLE;
            for (long i = startTime; i < endTime; i += unit) {
                requests.add(new BinanceCoinPriceRequestDto(symbol, i, i + unit));
            }
        }
        return requests;
    }

    private long getStartTimeFromBinanceApi(String symbol) {
        long startTime = 0L;
        long endTime = BinanceUtil.getNowTimeUTC();
        List<CoinPrice> coinPrices = new ArrayList<>(List.of());
        String url = "https://api.binance.com/api/v3/uiKlines?symbol=" + symbol
            + "&interval=" + INTERVAL + "&startTime=" + startTime + "&endTime=" + endTime + "&limit=1";
        String response = restTemplate.getForObject(url, String.class);
        try {
            List<BinanceCoinPriceDTO> coinPriceDTOs = objectMapper.readValue(response,
                new TypeReference<List<BinanceCoinPriceDTO>>() {
                });
            return coinPriceDTOs.get(0).getOpenTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get {symbol} start time from Binance API");
        }
    }
}
