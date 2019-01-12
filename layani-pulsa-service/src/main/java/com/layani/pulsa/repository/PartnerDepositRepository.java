package com.layani.pulsa.repository;

import com.layani.pulsa.entity.PartnerDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface PartnerDepositRepository
		extends
			JpaRepository<PartnerDeposit, Long> {

	@Query("SELECT p FROM PartnerDeposit p WHERE p.partnerId.id = :partnerId")
	PartnerDeposit findPartnerDepositById(@Param("partnerId") Long partnerId);
}