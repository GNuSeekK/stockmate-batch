package com.stockmate.batch.hankook.websocket;

import com.stockmate.batch.entity.StockTick;
import com.stockmate.batch.entity.StockTickId;
import com.stockmate.batch.service.StockTickService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class HankookTickDataHandler extends TextWebSocketHandler {

    private final StockTickService stockTickService;
    private final BlockingQueue<StockTick> stockDataQueue = new LinkedBlockingQueue<>();
    private static final Integer BULK_SIZE = 1000;
    private static final Integer THREAD_SLEEP_TIME = 500;
    private static final Integer CLOSE_HOUR = 15;
    private static final Integer CLOSE_MINUTE = 30;
    private static final Integer DATA_NUM = 46;

    @Autowired
    public HankookTickDataHandler(StockTickService stockTickService) {
        this.stockTickService = stockTickService;
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true) {
                try {
                    List<StockTick> stockDataList = new ArrayList<>();
                    stockDataQueue.drainTo(stockDataList, BULK_SIZE); // 1000개씩 가져오기
                    saveStockTick(stockDataList);
                    queueCheck();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void queueCheck() throws InterruptedException {
        if (stockDataQueue.size() < BULK_SIZE) {
            Thread.sleep(THREAD_SLEEP_TIME); // 0.5초마다 한 번씩 실행
        }
    }

    private void saveStockTick(List<StockTick> stockDataList) {
        if (!stockDataList.isEmpty()) {
            stockTickService.bulkSaveAllTick(stockDataList);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        LocalDateTime receiveTime = LocalDateTime.now();
        List<StockTick> stockDataList = parseMessage(message.getPayload(), receiveTime);
        stockDataList.forEach(this::addStockData);
    }

    private void addStockData(StockTick stockData) {
        if (stockData.getId() != null) {
            stockDataQueue.add(stockData);
        }
    }

    private List<StockTick> parseMessage(String payload, LocalDateTime receiveTime) {
        List<StockTick> stockDataList = new ArrayList<>();
        if (receiveTime.getHour() >= CLOSE_HOUR && receiveTime.getMinute() >= CLOSE_MINUTE) {
            receiveTime = receiveTime.withHour(CLOSE_HOUR).withMinute(CLOSE_MINUTE).withSecond(0).withNano(0);
        }
        if (payload.startsWith("0|H0STCNT0")) {
            String[] data = payload.split("\\|");
            Integer dataNum = Integer.parseInt(data[2]);
            String[] stockData = data[3].split("\\^");
            for (int i = 0; i < dataNum; i++) {
                StockTick stockTick = StockTick.builder()
                    .id(StockTickId.builder()
                        .code(stockData[0 + i * DATA_NUM])
                        .date(receiveTime)
                        .price(Integer.parseInt(stockData[2 + i * DATA_NUM]))
                        .build())
                    .volume(Integer.parseInt(stockData[12 + i * DATA_NUM]))
                    .build();
                stockDataList.add(stockTick);
                // code, date, price, volume logging
                log.info("code: {}, date: {}, price: {}, volume: {}", stockTick.getId().getCode(),
                    stockTick.getId().getDate(), stockTick.getId().getPrice(), stockTick.getVolume());
            }
        }
        return stockDataList;
    }
}
