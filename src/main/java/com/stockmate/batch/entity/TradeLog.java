package com.stockmate.batch.entity;

import com.stockmate.batch.binance.feign.dto.OrderResponse;
import com.stockmate.batch.entity.base.BaseTimeEntity;
import com.stockmate.batch.quant.dto.CoinTradeResponseDTO;
import com.stockmate.batch.util.BinanceUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor
@IdClass(TradeLogId.class)
@Entity
public class TradeLog extends BaseTimeEntity implements Persistable<TradeLogId> {

    public TradeLog(Account account, CoinTradeResponseDTO coinTradeDTO) {
        this.id = account.getId();
        this.symbol = coinTradeDTO.getSymbol();
        this.buyTime = coinTradeDTO.getNanoTime() == 0 ? coinTradeDTO.getTime() : coinTradeDTO.getNanoTime();
        this.totalBuyAmount = coinTradeDTO.getAmount();
        this.totalBuyPrice = coinTradeDTO.getTotalPrice();
        this.buyFee = coinTradeDTO.getFee();
        this.buyDateTime = LocalDateTime.ofEpochSecond(coinTradeDTO.getTime() / 1000, 0, ZoneOffset.UTC);
        this.recentSellPrice = 0;
        this.recentSellTime = 0;
        this.totalSellAmount = BigDecimal.ZERO;
        this.totalSellPrice = 0;
        this.totalSellFee = 0;
        this.isComplete = false;
    }

    @Id
    @Column(name = "account_id")
    private long id;
    @Id
    private String symbol;
    // 시간
    @Id
    private long buyTime;
    private long sellTime;

    // 거래 완료 되었는가
    @ColumnDefault("false")
    private boolean isComplete;

    private LocalDateTime buyDateTime;
    private LocalDateTime sellDateTime;

    // 최근 판매가, 최근 판매 시간
    @ColumnDefault("0")
    private double recentSellPrice;
    @ColumnDefault("0")
    private long recentSellTime;


    private String strategy; // 전략 getName 으로 만들 예정

    // 구매 수수료
    private double buyFee;
    // 총 구매액
    private double totalBuyPrice;
    // 총 구매량
    // 소수 셋째자리 까지
    @Column(precision = 10, scale = 3)
    private BigDecimal totalBuyAmount;

    // 판매 수수료
    private double totalSellFee;
    // 총 판매액
    private double totalSellPrice;
    // 총 판매량
    @Column(precision = 10, scale = 3)
    private BigDecimal totalSellAmount;

    @Column(length = 1000)
    private String memo;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id")
//    private Account account;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "symbol")
//    private Coin coin;


    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }


    @Override
    public TradeLogId getId() {
        return new TradeLogId(id, symbol, buyTime);
    }

    public void sell(OrderResponse order) {
        CoinTradeResponseDTO coinTradeDTO = new CoinTradeResponseDTO(order);
        if (coinTradeDTO.getAmount().equals(BigDecimal.ZERO)) {
            return;
        }
        this.totalSellPrice += coinTradeDTO.getTotalPrice();
        this.totalSellAmount = this.totalSellAmount.add(coinTradeDTO.getAmount());
        this.totalSellFee += coinTradeDTO.getFee();
        if (Objects.equals(this.totalBuyAmount, this.totalSellAmount)) {
            this.sellTime = coinTradeDTO.getTime();
            this.sellDateTime = BinanceUtil.getTime(this.sellTime);
            if (this.recentSellPrice == 0) {
                this.recentSellPrice = coinTradeDTO.getTotalPrice() / coinTradeDTO.getAmount().doubleValue();
                this.recentSellTime = coinTradeDTO.getTime();
            }
        } else {
            this.recentSellPrice = coinTradeDTO.getTotalPrice() / coinTradeDTO.getAmount().doubleValue();
            this.recentSellTime = coinTradeDTO.getTime();
        }
        if (Objects.equals(this.totalBuyAmount, this.totalSellAmount)) {
            this.isComplete = true;
        }
    }

    public double getBuyPrice() {
        return totalBuyPrice / totalBuyAmount.doubleValue();
    }

    public TradeLog addMemo(String memo) {
        if (this.memo == null) {
            this.memo = memo;
        } else {
            this.memo += "\n" + memo;
        }
        return this;
    }

    public BigDecimal getRemainAmount() {
        return this.totalBuyAmount.subtract(this.totalSellAmount);
    }

    public double getTotalFee() {
        return this.buyFee + this.totalSellFee;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public void update(OrderResponse order) {
        this.totalSellAmount = this.totalSellAmount.add(new BigDecimal(order.getExecutedQty()));
    }

    public void updateBuyDateTime(LocalDateTime buyDateTime) {
        this.buyDateTime = buyDateTime;
    }

    public long getAccountId() {
        return id;
    }
}
