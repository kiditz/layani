package com.layani.pulsa.repository;

import com.layani.pulsa.entity.OrderMessageMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface OrderMessageMappingRepository
		extends
			JpaRepository<OrderMessageMapping, Long> {

	@Query("SELECT m FROM OrderMessageMapping m JOIN m.partnerId p WHERE p.id = :partnerId AND lower(:partnerMessage) like lower(concat('%', m.partnerMessage, '%'))")
	OrderMessageMapping findMessageMapping(@Param("partnerId") Long partnerId, @Param("partnerMessage") String partnerMessage);
}