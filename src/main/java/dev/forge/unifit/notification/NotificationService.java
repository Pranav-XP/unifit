package dev.forge.unifit.notification;

import dev.forge.unifit.booking.BookingNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAdmin(BookingNotification notification){
        messagingTemplate.convertAndSend("/topic/admin",notification);
    }
}
