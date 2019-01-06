package com.layani.pulsa.repository;

import com.layani.pulsa.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o WHERE o.id = :orderId")
	public Order findOrderById(@Param("orderId") Long orderId);
}