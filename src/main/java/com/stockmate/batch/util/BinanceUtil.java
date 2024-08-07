package com.stockmate.batch.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class BinanceUtil {

    public static final String COIN = "BTCUSDT";
    public static final long ONE_MINUTE = 60 * 1000;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final Double LEVERAGE = 20.0;
    public static final Double FEE = 0.05 / 100; // 0.045% * 20 (레버리지)

    public static long getNowTimeUTC() {
        return LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    public static long getTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    public static LocalDateTime getTime(long time) {
        return LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC);
    }

    public static int getLastDay(int year, int month) {
        return LocalDateTime.of(year, month, 1, 0, 0).toLocalDate().lengthOfMonth();
    }

    public static void main(String[] args) {
        long time = getTime(2023, 2, 1, 0, 0);
        System.out.println(LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC));
    }

}
