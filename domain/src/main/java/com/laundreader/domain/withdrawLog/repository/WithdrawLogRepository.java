package com.laundreader.domain.withdrawLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.withdrawLog.entity.WithdrawLog;

public interface WithdrawLogRepository extends JpaRepository<WithdrawLog, Long> {
}
