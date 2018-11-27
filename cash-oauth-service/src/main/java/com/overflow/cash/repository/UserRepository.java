package com.overflow.cash.repository;

import com.overflow.cash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findUserPrincipalByPhoneNumberOrUsername(String phoneNumber, String username);
}