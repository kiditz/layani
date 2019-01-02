package com.layani.pulsa.repository;

import com.layani.pulsa.entity.DepositLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositLogRepository extends JpaRepository<DepositLog, Long> {
}