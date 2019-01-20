package com.layani.pulsa.repository;

import com.layani.pulsa.entity.OrderPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderPayloadRepository
		extends
			JpaRepository<OrderPayload, Long> {

	@Query("SELECT p FROM OrderPayload p JOIN p.orderId o WHERE o.reqid = :reqid")
	Page<OrderPayload> findOrderPayloadByReqid(@Param("reqid") String reqid, Pageable pageable);
}