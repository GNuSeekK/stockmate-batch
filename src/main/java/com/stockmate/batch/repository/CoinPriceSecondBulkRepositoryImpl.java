package com.stockmate.batch.repository;

import com.stockmate.batch.entity.CoinPriceSecond;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class CoinPriceSecondBulkRepositoryImpl implements
    CoinPriceSecondBulkRepository {

    private final EntityManager em;

    @Override
    @Transactional
    public void bulkSaveAllCoinPrice(List<CoinPriceSecond> coinPrices) {
        if (coinPrices.isEmpty()) {
            return;
        }
        StringBuilder sql = new StringBuilder(
            "INSERT IGNORE INTO coin_price_second (coin_id, open_time, open_price, high_price, low_price, close_price, volume, quote_asset_volume, number_of_trades, created_date, last_modified_date) VALUES ");

        coinPrices.forEach(coinPrice -> inputToSql(coinPrice, sql));
        String query = eraseLastTwo(sql) + " ON DUPLICATE KEY UPDATE "
            + "open_price = VALUES(open_price), "
            + "high_price = VALUES(high_price), "
            + "low_price = VALUES(low_price), "
            + "close_price = VALUES(close_price), "
            + "volume = VALUES(volume), "
            + "quote_asset_volume = VALUES(quote_asset_volume), "
            + "number_of_trades = VALUES(number_of_trades), "
            + "last_modified_date = NOW();";
        em.createNativeQuery(query).executeUpdate();

    }

    private static String eraseLastTwo(StringBuilder sql) {
        return sql.substring(0, sql.length() - 2);
    }

    private static void inputToSql(CoinPriceSecond coinPrice, StringBuilder sql) {
        sql.append("(")
            .append(coinPrice.getRealId()).append(", ")
            .append(coinPrice.getOpenTime()).append(", ")
            .append(coinPrice.getOpenPrice()).append(", ")
            .append(coinPrice.getHighPrice()).append(", ")
            .append(coinPrice.getLowPrice()).append(", ")
            .append(coinPrice.getClosePrice()).append(", ")
            .append(coinPrice.getVolume()).append(", ")
            .append(coinPrice.getQuoteAssetVolume()).append(", ")
            .append(coinPrice.getNumberOfTrades()).append(", ")
            .append("NOW(), NOW()), ");
    }
}
