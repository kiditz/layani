package com.overflow.cash.repository;

import com.overflow.cash.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
	@Query("SELECT a.authority FROM Authority a WHERE a.userId.phoneNumber = :phoneNumber")
	List<Authority> getAuthorityByUsername(@Param("phoneNumber") String username);
}