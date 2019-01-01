package com.layani.pulsa.repository;

import com.layani.pulsa.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends	JpaRepository<Notification, Long> {

}