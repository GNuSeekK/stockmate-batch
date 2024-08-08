package com.stockmate.batch.quant.trader;

import com.stockmate.batch.binance.feign.dto.OrderResponse;
import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.dto.BTCTradeRequestDTO;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!real")
public class FakeTrader implements Trader {

    @Override
    public Optional<TradeLog> buy(Account account, CoinPriceDTO coinPrice, double buyPercent) {
        log.info("구매를 시작합니다");
        BTCTradeRequestDTO btcTradeRequestDTO = new BTCTradeRequestDTO(account, coinPrice, buyPercent);
        if (btcTradeRequestDTO.getQuantity().equals(BigDecimal.ZERO)) {
            log.info("구매 수량이 0개 입니다");
            return Optional.empty();
        }
        OrderResponse order = OrderResponse.fakeResponseOf(btcTradeRequestDTO, coinPrice);
        account.buy(order);
        log.info("구매 가격 {}, 수량 {}", order.getCumQuote(), order.getExecutedQty());
        TradeLog tradeLog = order.toTradeLog(account);
        return Optional.of(tradeLog);
    }

    @Override
    public TradeLog sellAll(Account account, TradeLog tradeLog, CoinPriceDTO coinPrice) {
        BTCTradeRequestDTO btcTradeRequestDTO = new BTCTradeRequestDTO(tradeLog, 1.0);
        if (btcTradeRequestDTO.getQuantity().equals(BigDecimal.ZERO)) {
            log.info("판매 수량이 0개 입니다");
            return tradeLog;
        }
        OrderResponse order = OrderResponse.fakeResponseOf(btcTradeRequestDTO, coinPrice);
        tradeLog.sell(order);
        account.sell(order);
        double profit = tradeLog.getTotalSellPrice() - tradeLog.getTotalBuyPrice() - tradeLog.getTotalFee();
        account.addProfit(profit);
        double profitRate = Math.floor(profit / tradeLog.getTotalBuyPrice() * 100 * 10000) / 10000.0;
        log.info("판매 가격 {}, 수량 {}, 수익률 {}",
            tradeLog.getTotalSellPrice(),
            tradeLog.getTotalSellAmount(),
            profitRate);
        tradeLog.addMemo("수익률 : " + profitRate + "%\n");
        return tradeLog;
    }

    @Override
    public TradeLog takeProfit(Account account, TradeLog tradeLog, CoinPriceDTO coinPrice, double takeProfitPercent) {
        BTCTradeRequestDTO btcTradeRequestDTO = new BTCTradeRequestDTO(tradeLog, takeProfitPercent);
        if (btcTradeRequestDTO.getQuantity().equals(BigDecimal.ZERO)) {
            log.info("판매 수량이 0개 입니다");
            return tradeLog;
        }
        OrderResponse order = OrderResponse.fakeResponseOf(btcTradeRequestDTO, coinPrice);
        tradeLog.sell(order);
        account.sell(order);
        return tradeLog;
    }
}
