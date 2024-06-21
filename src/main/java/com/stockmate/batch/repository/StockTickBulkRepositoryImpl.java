package com.stockmate.batch.repository;

import com.stockmate.batch.entity.StockTick;
import jakarta.persistence.EntityManager;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class StockTickBulkRepositoryImpl implements StockTickBulkRepository {

    private final EntityManager em;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss.SSSSSS");

    @Transactional
    @Override
    public void bulkSaveAllTick(List<StockTick> stockTicks) {
        // bulk insert with native query
        // insert ~~ on duplicate key update ~~
        StringBuilder sql = new StringBuilder(
            "INSERT INTO stock_tick (stock_code, original_date, price, volume) VALUES ");

        for (StockTick stockTick : stockTicks) {
            sql.append("(")
                .append("'").append(stockTick.getId().getCode()).append("', ")
                .append("'").append(stockTick.getId().getDate().format(DATE_TIME_FORMATTER)).append("', ")
                .append(stockTick.getId().getPrice()).append(", ")
                .append(stockTick.getVolume()).append("), ");
        }
        String query = sql.substring(0, sql.length() - 2);
        query += " ON DUPLICATE KEY UPDATE volume = volume + VALUES(volume)";
        log.info(query);
        em.createNativeQuery(query).executeUpdate();
    }
}
