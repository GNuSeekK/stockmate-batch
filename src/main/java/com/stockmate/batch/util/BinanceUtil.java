package com.stockmate.batch.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class BinanceUtil {

    public static final long ONE_MINUTE = 60 * 1000;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final Double FEE = 0.07125;

    public static long getNowTimeUTC() {
        return LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) * 1000;
    }

}
