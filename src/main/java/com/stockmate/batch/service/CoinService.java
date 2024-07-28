package com.stockmate.batch.service;


import com.stockmate.batch.entity.Coin;
import com.stockmate.batch.repository.CoinRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinService {

    private final CoinRepository coinRepository;

    public List<Coin> getCoinSymbols() {
        return coinRepository.findAll();
    }

    public Coin getCoinBySymbol(String symbol) {
        return coinRepository.findBySymbol(symbol).orElseThrow(() -> new IllegalArgumentException("해당 코인이 존재하지 않습니다."));
    }
}
