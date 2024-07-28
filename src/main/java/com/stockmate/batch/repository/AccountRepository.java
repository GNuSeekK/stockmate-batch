package com.stockmate.batch.repository;

import com.stockmate.batch.entity.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT a FROM Account a ORDER BY a.id DESC limit 1")
    Account findLastestAccount();


    Optional<Account> findByAccountNo(String accountNo);

}
