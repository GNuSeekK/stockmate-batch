package com.stockmate.batch.quant.trader;

import com.stockmate.batch.binance.feign.dto.OrderResponse;
import com.stockmate.batch.binance.service.BinanceFutureService;
import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.dto.BTCTradeRequestDTO;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.slack.SlackService;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("real")
public class RealTrader implements Trader {

    private final BinanceFutureService binanceFutureService;
    private final SlackService slackService;

    @Override
    public Optional<TradeLog> buy(Account account, CoinPriceDTO coinPrice, double buyPercent) {
        log.info("구매를 시작합니다");
        BTCTradeRequestDTO btcTradeRequestDTO = new BTCTradeRequestDTO(account, coinPrice, buyPercent);
        if (btcTradeRequestDTO.getQuantity().equals(BigDecimal.ZERO)) {
            log.info("구매 수량이 0개 입니다");
            return Optional.empty();
        }
        OrderResponse order = binanceFutureService.order(btcTradeRequestDTO);
        account.buy(order);
        log.info("구매 가격 {}, 수량 {}", order.getCumQuote(), order.getExecutedQty());
        TradeLog tradeLog = order.toTradeLog(account);
        makeBuyMemoAndMessage(coinPrice, order, tradeLog);
        return Optional.of(tradeLog);
    }


    @Override
    public TradeLog sellAll(Account account, TradeLog tradeLog, CoinPriceDTO coinPrice) {
        BTCTradeRequestDTO btcTradeRequestDTO = new BTCTradeRequestDTO(tradeLog, 1.0);
        if (btcTradeRequestDTO.getQuantity().equals(BigDecimal.ZERO)) {
            log.info("판매 수량이 0개 입니다");
            return tradeLog;
        }
        OrderResponse order = binanceFutureService.order(btcTradeRequestDTO);
        tradeLog.sell(order);
        account.sell(order);
        double profit = tradeLog.getTotalSellPrice() - tradeLog.getTotalBuyPrice() - tradeLog.getTotalFee();
        account.addProfit(profit);
        double profitRate = Math.floor(profit / tradeLog.getTotalBuyPrice() * 100 * 10000) / 10000.0;
        log.info("판매 가격 {}, 수량 {}, 수익률 {}",
            tradeLog.getTotalSellPrice(),
            tradeLog.getTotalSellAmount(),
            profitRate);
//        tradeLog.addMemo("평균 판매 가격 : " + tradeLog.getTotalSellPrice() / tradeLog.getTotalSellAmount() + "\n");
        tradeLog.addMemo("수익률 : " + profitRate + "%\n");
        sendSellAllSlack(tradeLog, order, profit, profitRate);
        return tradeLog;
    }

    private void sendSellAllSlack(TradeLog tradeLog, OrderResponse order, double profit, double profitRate) {
        String message = "거래 형태 - 익절 \n"
            + "일시 - " + order.getUpdateTime() + "\n"
            + "판매 가격 - " + order.getCumQuote() + "\n"
            + "수량 - " + order.getExecutedQty() + "\n"
            + "평균 판매 가격 - " + tradeLog.getTotalSellPrice() / tradeLog.getTotalSellAmount().doubleValue() + "\n"
            + "평균 구매 가격 - " + tradeLog.getBuyPrice() + "\n"
            + "수익 - " + profit + "\n"
            + "수익률 - " + profitRate + "%";
        slackService.sendMessage(message);
    }

    @Override
    public TradeLog takeProfit(Account account, TradeLog tradeLog, CoinPriceDTO coinPrice, double takeProfitPercent) {
        BTCTradeRequestDTO btcTradeRequestDTO = new BTCTradeRequestDTO(tradeLog, takeProfitPercent);
        if (btcTradeRequestDTO.getQuantity().equals(BigDecimal.ZERO)) {
            log.info("판매 수량이 0개 입니다");
            return tradeLog;
        }
        OrderResponse order = binanceFutureService.order(btcTradeRequestDTO);
        tradeLog.sell(order);
        account.sell(order);
        // 수익 실현 발생
        makeTakeProfitMemoAndMessage(tradeLog, order);
        return tradeLog;
    }

    private void makeTakeProfitMemoAndMessage(TradeLog tradeLog, OrderResponse order) {
        double profit = Double.parseDouble(order.getAvgPrice()) - tradeLog.getBuyPrice() - tradeLog.getTotalFee();
        double profitRate = Math.floor(profit / tradeLog.getBuyPrice() * 100 * 10000) / 10000.0;
        String message = "거래 형태 - 수익 실현 \n"
            + "일시 - " + order.getUpdateTime() + "\n"
            + "판매 가격 - " + order.getCumQuote() + "\n"
            + "수량 - " + order.getExecutedQty() + "\n"
            + "평균 판매 가격 - " + order.getAvgPrice() + "\n"
            + "평균 구매 가격 - " + tradeLog.getBuyPrice() + "\n"
            + "수익 - " + profit + "\n"
            + "수익률 - " + profitRate + "%";
        slackService.sendMessage(message);
    }


    private void makeBuyMemoAndMessage(CoinPriceDTO coinPrice, OrderResponse order, TradeLog tradeLog) {
        double slipPricePercent =
            (coinPrice.getClosePrice() - Double.parseDouble(order.getAvgPrice())) / coinPrice.getClosePrice() * 100;
        slipPricePercent = Math.floor(slipPricePercent * 10000) / 10000.0;
        tradeLog.addMemo("슬리피지 : " + slipPricePercent);
        // message 사용 할 것. 거래 형태 - () \n 일시 - () \n 구매 가격 - () \n 수량 - () \n 평균 구매 가격 - () \n 슬리피지 - ()
        String message = "거래 형태 - 구매 \n"
            + "일시 - " + order.getUpdateTime() + "\n"
            + "구매 가격 - " + order.getCumQuote() + "\n"
            + "수량 - " + order.getExecutedQty() + "\n"
            + "평균 구매 가격 - " + order.getAvgPrice() + "\n"
            + "슬리피지 - " + slipPricePercent + "%";
        slackService.sendMessage(message);
    }
}
