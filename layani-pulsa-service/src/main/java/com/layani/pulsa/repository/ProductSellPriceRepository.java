package com.layani.pulsa.repository;

import com.layani.pulsa.entity.ProductSellPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSellPriceRepository
		extends
			JpaRepository<ProductSellPrice, Long> {
}