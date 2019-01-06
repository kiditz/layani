package com.layani.pulsa.repository;

import com.layani.pulsa.entity.OrderPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface OrderPayloadRepository
		extends
			JpaRepository<OrderPayload, Long> {

	@Query("SELECT p FROM OrderPayload p JOIN p.orderId o WHERE o.reqid = :reqid")
	public OrderPayload findOrderPayloadByReqid(@Param("reqid") String reqid);
}