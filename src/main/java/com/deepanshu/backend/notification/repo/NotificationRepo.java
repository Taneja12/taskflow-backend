package com.deepanshu.backend.notification.repo;

import com.deepanshu.backend.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, UUID> {

    Page<Notification> getByRecipientId(UUID recipientId, Pageable pageable);

    Notification getByIdAndRecipientId(UUID notificationId, UUID id);
}
