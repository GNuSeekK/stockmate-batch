package com.stockmate.batch.binance.feign.dto;

import com.stockmate.batch.entity.Account;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class AccountInfo {

    private int feeTier;
    private boolean feeBurn;
    private boolean canDeposit;
    private boolean canWithdraw;
    private long updateTime;
    private boolean multiAssetsMargin;
    private long tradeGroupId;
    private String totalInitialMargin;
    private String totalMaintMargin;
    private String totalWalletBalance;
    private String totalUnrealizedProfit;
    private String totalMarginBalance;
    private String totalPositionInitialMargin;
    private String totalOpenOrderInitialMargin;
    private String totalCrossWalletBalance;
    private String totalCrossUnPnl;
    private String availableBalance;
    private String maxWithdrawAmount;
    private List<Asset> assets;
//    private List<Position> positions;

    public Account toEntity() {
        Asset usdt = assets.stream()
            .filter(asset -> asset.getAsset().equals("USDT"))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("USDT가 없습니다."));
        return Account.builder()
            .name("binance")
            .totalAsset(Double.parseDouble(usdt.getWalletBalance()))
            .cash(Double.parseDouble(availableBalance))
            .unit("USDT")
            .startAsset(Double.parseDouble(totalWalletBalance))
            .startDate(LocalDateTime.now())
            .build();
    }
}
