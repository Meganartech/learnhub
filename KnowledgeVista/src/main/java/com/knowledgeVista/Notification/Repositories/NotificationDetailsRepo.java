package com.knowledgeVista.Notification.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Notification.NotificationDetails;

@Repository
public interface NotificationDetailsRepo extends JpaRepository<NotificationDetails, Long> {

	@Modifying
    @Query("DELETE FROM NotificationDetails nu WHERE nu.notifyId = :notificationId")
    void deleteByNotifyId(@Param("notificationId") Long notificationId);
}
