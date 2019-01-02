package com.layani.pulsa.repository;

import com.layani.pulsa.entity.PartnerProductPurchasePrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerProductPurchasePriceRepository
		extends
			JpaRepository<PartnerProductPurchasePrice, Long> {
}