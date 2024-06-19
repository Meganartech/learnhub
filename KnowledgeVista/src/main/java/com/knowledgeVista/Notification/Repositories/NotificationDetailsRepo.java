package com.knowledgeVista.Notification.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Notification.NotificationDetails;

@Repository
public interface NotificationDetailsRepo extends JpaRepository<NotificationDetails, Long> {

}
