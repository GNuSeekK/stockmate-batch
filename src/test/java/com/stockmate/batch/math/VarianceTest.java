package com.stockmate.batch.math;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VarianceTest {


    @Test
    @DisplayName("실제 계산 테스트")
    public void calcReal() throws Exception {
        System.out.println("실제 계산 테스트 ================================");
        // given
        List<Double> list = getDoubleList();
        double sum = list.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / list.size();
        double finalAvg = avg;
        double variance = list.stream().mapToDouble(a -> Math.pow(a - finalAvg, 2)).sum() / list.size();

        LocalDateTime now = LocalDateTime.now();
        // when
        for (int i = 0; i < 10000; i++) {
            double oldValue = list.get(list.size() - 1);
            double newValue = 10000.0 + i;
            changeLast(list, newValue);
            double newSum = sum - oldValue + newValue;

            sum = newSum;
            avg = newSum / list.size();
            double finalAvg1 = avg;
            variance = list.stream().mapToDouble(a -> Math.pow(a - finalAvg1, 2)).sum() / list.size();
        }
        // then - 시간 측정
        System.out.println("실제 계산 소요 시간 : " + LocalDateTime.now().minusNanos(now.getNano()).getNano());
        // 마지막 variance 값 확인
        System.out.println("마지막 variance 값 : " + new BigDecimal(variance).toString());
    }


    @Test
    @DisplayName("계산식 테스트")
    public void calcWithMath() throws Exception {
        System.out.println("계산식 테스트 ================================");
        // given
        List<Double> list = getDoubleList();
        double sum = list.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / list.size();
        double finalAvg = avg;
        double variance = list.stream().mapToDouble(a -> Math.pow(a - finalAvg, 2)).sum() / list.size();

        LocalDateTime now = LocalDateTime.now();
        // when
        for (int i = 0; i < 10000; i++) {
            double oldValue = list.get(list.size() - 1);
            double newValue = 10000.0 + i;
            changeLast(list, newValue);
            double newSum = sum - oldValue + newValue;
            double newAvg = newSum / list.size();
            double newVariance = variance + (newValue - oldValue) * (newValue - newAvg + oldValue - avg) / list.size();
            // 계산 완료, old 값들을 새로운 값으로 변경

            sum = newSum;
            avg = newAvg;
            variance = newVariance;
        }
        // then
        System.out.println("계산식 활용 소요 시간 : " + LocalDateTime.now().minusNanos(now.getNano()).getNano());
        // 마지막 variance 값 확인
        System.out.println("마지막 variance 값 : " + variance);
    }

    private List<Double> getDoubleList() {
        // 1 ~ 1000 까지
        List<Double> list = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            list.add((double) i);
        }
        return list;
    }

    private void changeLast(List<Double> list, Double value) {
        list.set(list.size() - 1, value);
    }
}
