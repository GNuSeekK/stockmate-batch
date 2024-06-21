package com.stockmate.batch.service;

import com.stockmate.batch.entity.StockTick;
import com.stockmate.batch.repository.StockTickRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockTickService {

    private final StockTickRepository stockTickRepository;

    @Transactional
    public void bulkSaveAllTick(List<StockTick> stockTicks) {
        stockTickRepository.bulkSaveAllTick(stockTicks);
    }

}
