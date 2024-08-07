package com.stockmate.batch.entity;

import com.stockmate.batch.entity.base.BaseTimeEntity;
import com.stockmate.batch.util.BinanceUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor
@Entity
@IdClass(AccountLogId.class)
public class AccountLog extends BaseTimeEntity implements Persistable<AccountLogId> {

    @Builder
    public AccountLog(long id, long beforeTime, long afterTime, double totalAsset,
        double profitRate) {
        this.id = id;
        this.beforeTime = beforeTime;
        this.afterTime = afterTime;
        this.totalAsset = totalAsset;
        this.profitRate = profitRate;
        this.time = BinanceUtil.getTime(afterTime);
    }

    @Id
    @Column(name = "account_id", insertable = false, updatable = false)
    private long id;

    // 비교 시간
    @Id
    @Column(name = "before_time")
    private long beforeTime;
    // 로그 시간
    @Id
    @Column(name = "after_time")
    private long afterTime;

    // 평가 시점
    private LocalDateTime time;


    // 총 자산
    private double totalAsset;
    // 전일 대비 수익률
    private double profitRate;

//    @Id
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id", insertable = true, updatable = true)
//    private Account account;


    @Override
    public AccountLogId getId() {
        return new AccountLogId(id, beforeTime, afterTime);
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }

}
