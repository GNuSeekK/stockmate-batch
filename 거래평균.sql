select account_id,
       avg(((total_sell_price - total_buy_price - total_sell_fee - buy_fee) / total_buy_price) * 100) as avg_profit,
       count(*)                                                                                       as trades,
       sum((total_sell_price - total_buy_price - total_sell_fee - buy_fee))                           as sum_profit
from trade_log
where sell_time != 0
group by account_id