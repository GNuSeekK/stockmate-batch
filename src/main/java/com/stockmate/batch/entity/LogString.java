package com.stockmate.batch.entity;

import com.stockmate.batch.entity.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
public class LogString extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String log;

    public LogString(String log) {
        this.log = log;
    }

    public void addLog(String text) {
        if (log == null) {
            log = text;
        } else {
            log += text;
        }
    }


}
