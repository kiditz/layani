package com.layani.pulsa.repository;

import com.layani.pulsa.entity.PartnerDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerDepositRepository
		extends
			JpaRepository<PartnerDeposit, Long> {
}