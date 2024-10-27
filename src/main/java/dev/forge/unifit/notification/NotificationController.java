package dev.forge.unifit.notification;


import dev.forge.unifit.booking.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Call this method when a new booking is created
    // Notify admin about new bookings
    public void notifyNewBookings(List<Booking> bookings) {
        for (Booking booking : bookings) {
            // Create a notification object with relevant booking details
            Map<String, Object> notification = new HashMap<>();
            notification.put("bookingId", booking.getId());
            notification.put("customerName", booking.getUser().getFirstName());
            notification.put("facilityName", booking.getFacility().getName());

            // Send the notification as a JSON string
            messagingTemplate.convertAndSend("/topic/admin", notification);
        }
    }
}
