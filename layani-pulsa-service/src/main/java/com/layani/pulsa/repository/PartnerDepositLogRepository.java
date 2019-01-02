package com.layani.pulsa.repository;

import com.layani.pulsa.entity.PartnerDepositLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerDepositLogRepository
		extends
			JpaRepository<PartnerDepositLog, Long> {
}