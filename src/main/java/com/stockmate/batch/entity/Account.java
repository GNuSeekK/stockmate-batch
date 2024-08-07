package com.stockmate.batch.entity;


import com.stockmate.batch.binance.feign.dto.OrderResponse;
import com.stockmate.batch.entity.base.BaseTimeEntity;
import com.stockmate.batch.util.BinanceUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account", indexes = {
    @Index(name = "account_idx", columnList = "accountNo")
})
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private long id;

    private String accountNo;
    private int tradeNo;
    @Column(length = 1000)
    private String memo;
    private double profit;

    private String name;
    // 총 자산
    private double totalAsset;
    // 현금
    private double cash;
    private String unit;

    // 시작
    private double startAsset;
    // 시작일
    private LocalDateTime startDate;
    // 종료일
    private LocalDateTime endDate;

    private void useCash(double v) {
        this.cash -= v;
    }

    private void addCash(double v) {
        this.cash += v;
    }


    public boolean isEnoughCash(double ratio) {
        return this.cash >= totalAsset * ratio;
    }


    public void buy(OrderResponse orderResponse) {
        double fee = Double.parseDouble(orderResponse.getCumQuote()) * BinanceUtil.FEE;
        double total = Double.parseDouble(orderResponse.getCumQuote()) / BinanceUtil.LEVERAGE + fee;
        this.useCash(total);
        if (this.tradeNo == 0) {
            this.tradeNo = 1;
        } else {
            this.tradeNo++;
        }
    }


    public void sell(OrderResponse orderResponse) {
        double fee = Double.parseDouble(orderResponse.getCumQuote()) * BinanceUtil.FEE;
        double total = Double.parseDouble(orderResponse.getCumQuote()) / BinanceUtil.LEVERAGE - fee;
        this.addCash(total);
    }

    public void addProfit(double v) {
        this.totalAsset += v;
    }

    public void calculateProfit() {
        // 수익률을 %로 표기하며 소수 2번째 자리까지 표시
        this.profit = Math.floor((this.totalAsset - this.startAsset) / this.startAsset * 10000) / 100.0;
    }

    public boolean checkChange(Account account) {
        return this.totalAsset != account.totalAsset || this.cash != account.cash;
    }

    public Account copyForCheckChange() {
        return Account.builder()
            .totalAsset(this.totalAsset)
            .cash(this.cash)
            .build();
    }
}
