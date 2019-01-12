package com.layani.pulsa.repository;

import com.layani.pulsa.entity.ProductSellPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ProductSellPriceRepository
		extends
			JpaRepository<ProductSellPrice, Long> {

	@Query("SELECT p FROM ProductSellPrice p WHERE p.productId.id = :productId AND p.active = true AND p.productId.active = true AND :date BETWEEN p.startAt AND p.endAt")
	Page<ProductSellPrice> findProductSellPriceByProductId(@Param("productId") Long productId, @Param("date")Date date, Pageable pageable);
}