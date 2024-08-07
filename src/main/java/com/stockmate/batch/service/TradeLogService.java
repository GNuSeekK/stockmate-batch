package com.stockmate.batch.service;

import com.stockmate.batch.entity.TradeLog;
import com.stockmate.batch.repository.TradeLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeLogService {

    private final TradeLogRepository tradeLogRepository;

    //    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<TradeLog> findAllByIdAndComplete(long id, String symbol, boolean isComplete) {
        return tradeLogRepository.findAllByIdAndComplete(id, symbol, isComplete);
    }

    @Transactional
    public TradeLog save(TradeLog tradeLog) {
        return tradeLogRepository.save(tradeLog);
    }

    @Transactional
    public void saveAllWithBulk(List<TradeLog> completedLogs) {
        if (completedLogs.isEmpty()) {
            return;
        }
        tradeLogRepository.saveAllWithBulk(completedLogs);
    }
}
