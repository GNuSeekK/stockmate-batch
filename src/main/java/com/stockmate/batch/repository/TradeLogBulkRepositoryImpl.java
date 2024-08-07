package com.stockmate.batch.repository;

import com.stockmate.batch.entity.TradeLog;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TradeLogBulkRepositoryImpl implements TradeLogBulkRepository {

    private final EntityManager em;

    @Override
    public void saveAllWithBulk(List<TradeLog> completedLogs) {
        StringBuilder sql = new StringBuilder(
            "INSERT INTO trade_log (account_id, symbol, buy_time, sell_time, is_complete, buy_date_time, sell_date_time, recent_sell_price, recent_sell_time, total_buy_amount, total_buy_price, buy_fee, total_sell_amount, total_sell_price, total_sell_fee, memo) VALUES ");

        for (TradeLog tradeLog : completedLogs) {
            sql.append("(")
                .append(tradeLog.getAccountId()).append(", ")
                .append("'").append(tradeLog.getSymbol()).append("', ")
                .append(tradeLog.getBuyTime()).append(", ")
                .append(tradeLog.getSellTime()).append(", ")
                .append(tradeLog.isComplete()).append(", ")
                .append("'").append(tradeLog.getBuyDateTime()).append("', ")
                .append("'").append(tradeLog.getSellDateTime()).append("', ")
                .append(tradeLog.getRecentSellPrice()).append(", ")
                .append(tradeLog.getRecentSellTime()).append(", ")
                .append(tradeLog.getTotalBuyAmount()).append(", ")
                .append(tradeLog.getTotalBuyPrice()).append(", ")
                .append(tradeLog.getBuyFee()).append(", ")
                .append(tradeLog.getTotalSellAmount()).append(", ")
                .append(tradeLog.getTotalSellPrice()).append(", ")
                .append(tradeLog.getTotalSellFee()).append(", ")
                .append("'").append(tradeLog.getMemo()).append("'), ");
        }

        String query = sql.substring(0, sql.length() - 2);
        em.createNativeQuery(query).executeUpdate();


    }
}
