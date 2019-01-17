package com.layani.pulsa.repository;

import com.layani.pulsa.entity.OrderPostPaid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPostPaidRepository
		extends
			JpaRepository<OrderPostPaid, Long> {
}