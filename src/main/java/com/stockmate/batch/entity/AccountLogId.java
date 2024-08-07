package com.stockmate.batch.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class AccountLogId implements Serializable {

    private long id;
    private long beforeTime;
    private long afterTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountLogId that)) {
            return false;
        }
        return id == that.id && beforeTime == that.beforeTime && afterTime == that.afterTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, beforeTime, afterTime);
    }
}
