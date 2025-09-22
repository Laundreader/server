package com.laundreader.domain.repository.withdrawLog;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.entity.withdrawLog.WithdrawLog;

public interface WithdrawLogRepository extends JpaRepository<WithdrawLog, Long> {
}
