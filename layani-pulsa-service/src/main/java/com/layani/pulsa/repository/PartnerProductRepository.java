package com.layani.pulsa.repository;

import com.layani.pulsa.entity.PartnerProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerProductRepository
		extends
			JpaRepository<PartnerProduct, Long> {
}