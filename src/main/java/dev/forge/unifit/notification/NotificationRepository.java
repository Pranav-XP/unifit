package dev.forge.unifit.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Custom query to find unread notifications based on status
    @Query("SELECT n FROM Notification n WHERE n.status = :status")
    List<Notification> findUnreadNotificationsByStatus(@Param("status") NotificationStatus status);
}
