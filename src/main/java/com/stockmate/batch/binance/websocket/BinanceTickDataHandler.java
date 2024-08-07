package com.stockmate.batch.binance.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmate.batch.binance.dto.BinanceWebsocketDTO;
import com.stockmate.batch.quant.TradingExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceTickDataHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final TradingExecutor tradingExecutor;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            BinanceWebsocketDTO binanceWebsocketDTO = objectMapper.readValue(message.getPayload(),
                BinanceWebsocketDTO.class);
            tradingExecutor.trade(binanceWebsocketDTO);
            log.info("발생 시간 : {}, 가격 : {}, 처리 시간 : {}", binanceWebsocketDTO.getEventTime(),
                binanceWebsocketDTO.getPrice(), System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
