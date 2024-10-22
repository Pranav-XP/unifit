package dev.forge.unifit.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class NotificationController {

//    @MessageMapping("/sendNotification")
//    @SendTo("/topic/admin")
//    public String sendNotification(String message) {
//        return message;
//    }
//
//    @Autowired
//    private NotificationService notificationService;
//
//    @PostMapping("/sendNotification")
//    public String sendInappNotification(String message) {
//        notificationService.createNotification(message);
//        return "redirect:/notifications";
//    }
//
//    @GetMapping("/unreadNotifications")
//    public List<Notification> getUnreadNotifications() {
//        return notificationService.getUnreadNotifications();
//    }
//
//    @PostMapping("/markNotificationsAsRead")
//    public String markAsRead() {
//        notificationService.markNotificationsAsViewed();
//        return "redirect:/notifications";
//    }
}

