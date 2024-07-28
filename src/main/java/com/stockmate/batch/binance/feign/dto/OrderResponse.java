package com.stockmate.batch.binance.feign.dto;

import com.stockmate.batch.entity.Account;
import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.quant.dto.BTCTradeRequestDTO;
import com.stockmate.batch.quant.dto.CoinPriceDTO;
import com.stockmate.batch.quant.dto.CoinTradeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private String symbol;
    private long updateTime;
    private String avgPrice;
    private String cumQuote;
    private String executedQty;

//    private String clientOrderId;
//    private String cumQty;
//    private long orderId;
//    private String origQty;
//    private String price;
//    private boolean reduceOnly;
//    private String side;
//    private String positionSide;
//    private String status;
//    private String stopPrice;
//    private boolean closePosition;
//    private String timeInForce;
//    private String type;
//    private String origType;
//    private String activatePrice;
//    private String priceRate;
//    private String workingType;
//    private boolean priceProtect;
//    private String priceMatch;
//    private String selfTradePreventionMode;
//    private long goodTillDate;

    public TradeLog toTradeLog(Account account) {
        CoinTradeResponseDTO coinTradeResponseDTO = new CoinTradeResponseDTO(this);
        return new TradeLog(account, coinTradeResponseDTO);
    }

    public static OrderResponse fakeResponseOf(BTCTradeRequestDTO btcTradeRequestDTO, CoinPriceDTO coinPriceDTO) {
        return OrderResponse.builder()
            .symbol(btcTradeRequestDTO.getSymbol())
            .updateTime(System.currentTimeMillis())
            .avgPrice(String.valueOf(coinPriceDTO.getClosePrice()))
            .cumQuote(String.valueOf(coinPriceDTO.getClosePrice() * btcTradeRequestDTO.getQuantity().doubleValue()))
            .executedQty(String.valueOf(btcTradeRequestDTO.getQuantity()))
            .build();
    }
}

//{
//    "clientOrderId": "SYrQdcPeAwV94ADvXwH80C",
//    "cumQty": "0.002",
//    "cumQuote": "129.83600",
//    "executedQty": "0.002",
//    "orderId": 374535329451,
//    "avgPrice": "64918.00000",
//    "origQty": "0.002",
//    "price": "0.00",
//    "reduceOnly": false,
//    "side": "SELL",
//    "positionSide": "BOTH",
//    "status": "FILLED",
//    "stopPrice": "0.00",
//    "closePosition": false,
//    "symbol": "BTCUSDT",
//    "timeInForce": "GTC",
//    "type": "MARKET",
//    "origType": "MARKET",
//    "activatePrice": null,
//    "priceRate": null,
//    "updateTime": 1721285157366,
//    "workingType": "CONTRACT_PRICE",
//    "priceProtect": false,
//    "priceMatch": "NONE",
//    "selfTradePreventionMode": "NONE",
//    "goodTillDate": 0
//    }