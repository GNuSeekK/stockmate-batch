package com.stockmate.batch.binance.feign.dto;

import lombok.Getter;

@Getter
public class Asset {

    private String asset;
    private String walletBalance;
    private String unrealizedProfit;
    private String marginBalance;
    private String maintMargin;
    private String initialMargin;
    private String positionInitialMargin;
    private String openOrderInitialMargin;
    private String crossWalletBalance;
    private String crossUnPnl;
    private String availableBalance;
    private String maxWithdrawAmount;
    private boolean marginAvailable;
    private long updateTime;
}
