SELECT a.account_id                                                                                   AS account_id,
       AVG(((total_sell_price - total_buy_price - total_sell_fee - buy_fee) / total_buy_price) * 100) AS avg_profit,
       SUM(CASE
               WHEN (total_sell_price - total_buy_price - total_sell_fee - buy_fee) > 0
                   THEN 1
               ELSE 0
           END)                                                                                       AS profitable_trades,
       SUM(CASE
               WHEN (total_sell_price - total_buy_price - total_sell_fee - buy_fee) / total_buy_price > -0.003
                   THEN 1
               ELSE 0
           END)                                                                                       AS minimal_loss_trades,
       SUM(CASE
               WHEN (total_sell_price - total_buy_price - total_sell_fee - buy_fee) > 0
                   THEN 1
               ELSE 0
           END) / COUNT(*) *
       100                                                                                            AS profitable_trade_percentage,
       SUM(CASE
               WHEN (total_sell_price - total_buy_price - total_sell_fee - buy_fee) / total_buy_price > -0.003
                   THEN 1
               ELSE 0
           END) / COUNT(*) *
       100                                                                                            AS minimal_loss_trade_percentage,
       COUNT(*)                                                                                       AS trades,
       SUM((total_sell_price - total_buy_price - total_sell_fee - buy_fee))                           AS sum_profit,
       a.account_no                                                                                   AS account_no,
       a.memo                                                                                         AS memo
FROM trade_log
         JOIN stockchart.account a ON trade_log.account_id = a.account_id
WHERE sell_time != 0
GROUP BY account_id;
