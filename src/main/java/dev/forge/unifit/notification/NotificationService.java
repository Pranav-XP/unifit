package dev.forge.unifit.notification;

import dev.forge.unifit.booking.BookingController;
import dev.forge.unifit.booking.BookingNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAdmin(BookingNotification notification){
        messagingTemplate.convertAndSend("/topic/admin",notification);
    }

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findUnreadNotificationsByStatus(NotificationStatus.UNREAD);
    }

    // Method to save a notification
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

}
