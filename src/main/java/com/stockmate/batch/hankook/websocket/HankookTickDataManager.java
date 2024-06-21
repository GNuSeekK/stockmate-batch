package com.stockmate.batch.hankook.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmate.batch.entity.Stock;
import com.stockmate.batch.hankook.feign.HankookClientConfig;
import com.stockmate.batch.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
public class HankookTickDataManager {

    private final StockRepository stockRepository;
    private final HankookTickDataHandler hankookTickDataHandler;
    @Value("${hankook.socket}")
    private String url;
    private WebSocketSession session;
    private String approvalKey;


    @Autowired
    public HankookTickDataManager(
        HankookTickDataHandler hankookTickDataHandler,
        StockRepository stockRepository,
        HankookClientConfig hankookClientConfig
    ) {
        this.hankookTickDataHandler = hankookTickDataHandler;
        this.stockRepository = stockRepository;
        this.approvalKey = hankookClientConfig.getApprovalToken();
    }

    @PostConstruct
    public void connect() {
        List<Stock> stocks = stockRepository.findAll();
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            this.session = client.doHandshake(hankookTickDataHandler, url).get();
            for (Stock stock : stocks) {
                subscribe(stock.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 재연결 로직 추가 필요
        }
    }

    private void subscribe(String stockCode) {
        try {
            if (this.session != null && this.session.isOpen()) {
                this.session.sendMessage(makeMessage(stockCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextMessage makeMessage(String stockCode) throws JsonProcessingException {
        Request request = new Request();
        request.setCode(stockCode);
        request.setKey(this.approvalKey);
        ObjectMapper objectMapper = new ObjectMapper();
        return new TextMessage(objectMapper.writeValueAsString(request));
    }

    @Getter
    private class Request {

        @JsonProperty("header")
        private Header header;
        @JsonProperty("body")
        private Body body;

        public Request() {
            this.header = new Header();
            this.body = new Body();
        }

        public void setCode(String code) {
            this.body.input.setTrKey(code);
        }

        public void setKey(String key) {
            this.header.setApprovalKey(key);
        }
    }

    @Getter
    private class Header {

        @Setter
        @JsonProperty("approval_key")
        private String approvalKey;
        @JsonProperty("custtype")
        private String custType = "P";
        @JsonProperty("tr_type")
        private String trType = "1";
        @JsonProperty("content-type")
        private String contentType = "utf-8";

    }

    @Getter
    private class Body {

        @JsonProperty("input")
        private Input input;

        public Body() {
            this.input = new Input();
        }
    }

    @Getter
    private class Input {

        @JsonProperty("tr_id")
        private String trId = "H0STCNT0";
        @JsonProperty("tr_key")
        @Setter
        private String trKey;

    }


}
