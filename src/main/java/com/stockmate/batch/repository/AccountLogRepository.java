package com.stockmate.batch.repository;

import com.stockmate.batch.entity.AccountLog;
import com.stockmate.batch.entity.AccountLogId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLogRepository extends JpaRepository<AccountLog, AccountLogId> {

}
