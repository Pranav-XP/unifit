package dev.forge.unifit.notification;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/sendNotification")
    @SendTo("/topic/admin")
    public String sendNotification(String message) {
        return message;
    }
}

