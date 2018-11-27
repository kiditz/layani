package com.overflow.cash.repository;

import com.overflow.cash.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
	
	Client getByClientId(String clientId);
}