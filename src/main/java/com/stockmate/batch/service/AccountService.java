package com.stockmate.batch.service;

import com.stockmate.batch.entity.Account;
import com.stockmate.batch.repository.AccountRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;


    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> findByNo(String accountNo) {
        return accountRepository.findByAccountNo(accountNo);
    }
}
