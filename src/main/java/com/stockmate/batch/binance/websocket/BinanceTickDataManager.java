package com.stockmate.batch.binance.websocket;

import com.stockmate.batch.slack.SlackService;
import com.stockmate.batch.util.BinanceUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceTickDataManager {

    private final BinanceTickDataHandler binanceTickDataHandler;
    private final SlackService slackService;
    private WebSocketSession session;
    private LocalDateTime lastOpenTime;

    public void connect() {
        if (session != null && session.isOpen()) {
            if (lastOpenTime.plusHours(12).isAfter(LocalDateTime.now())) {
                // 12시간 이내에 연결된 경우 재연결하지 않음
                log.info("바이낸스 웹소켓 연결 유지 중");
                return;
            }
            try {
                session.close();
                log.info("바이낸스 웹소켓 연결 종료");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("바이낸스 웹소켓 연결 종료 실패");
                slackService.sendMessage("바이낸스 웹소켓 연결 종료 실패");
            }
        }
        String url = "wss://fstream.binance.com/ws/" + BinanceUtil.COIN.toLowerCase() + "@aggTrade";
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.doHandshake(binanceTickDataHandler, url).get();
            lastOpenTime = LocalDateTime.now();
            slackService.sendMessage("바이낸스 웹소켓 연결 성공");
        } catch (Exception e) {
            e.printStackTrace();
            slackService.sendMessage("바이낸스 웹소켓 연결 실패");
            // 재연결 로직 추가 필요
        }
    }

}
