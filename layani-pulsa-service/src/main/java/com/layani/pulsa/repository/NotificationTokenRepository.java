package com.layani.pulsa.repository;

import com.layani.pulsa.entity.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface NotificationTokenRepository
		extends
			JpaRepository<NotificationToken, String> {

	@Query("SELECT n FROM NotificationToken n WHERE n.userId = :userId")
	public NotificationToken findNotificationToken(@Param("userId") Long userId);
}