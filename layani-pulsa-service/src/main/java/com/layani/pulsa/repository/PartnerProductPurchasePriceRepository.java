package com.layani.pulsa.repository;

import com.layani.pulsa.entity.PartnerProductPurchasePrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface PartnerProductPurchasePriceRepository
		extends
			JpaRepository<PartnerProductPurchasePrice, Long> {

	@Query("SELECT pp FROM PartnerProductPurchasePrice pp JOIN pp.partnerProductId.products p WHERE p.id = :productId AND pp.active = true AND p.active = true AND :date BETWEEN pp.startAt AND pp.endAt")
	Page<PartnerProductPurchasePrice> findProductPurchasePrice(@Param("productId") Long productId, @Param("date") Date date, Pageable pageable);

}