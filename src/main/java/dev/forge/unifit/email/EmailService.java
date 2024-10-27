package dev.forge.unifit.email;


import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.event.Event;
import dev.forge.unifit.facility.FacilityService;
import dev.forge.unifit.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserService userService;

    private final TemplateEngine templateEngine;
    private final FacilityService facilityService;

    public EmailService(JavaMailSender mailSender, UserService userService, TemplateEngine templateEngine, FacilityService facilityService) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.templateEngine = templateEngine;
        this.facilityService = facilityService;
    }

    public void sendEventNotification(Event event) throws MessagingException {

        // Create email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true);
        } catch (MessagingException e) {
            throw new RuntimeException("MessageHelper could not be created", e);
        }

        List<String> recipients = userService.getAllUserEmailsExceptAdmin("admin@admin.com");
        helper.setTo("pranavchand777@gmail.com");
        helper.setSubject("UniFit Event - "+event.getEventName());
        String facilityName = facilityService.getFacility(event.getFacility().getId()).getName();
        // Prepare the context for the Thymeleaf template
        Context context = new Context();

        // Set the booking details and total price in the context
        context.setVariable("event", event);
        context.setVariable("facility", facilityName);

        // Load and populate the Thymeleaf template
        String htmlContent = templateEngine.process("event-email", context);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(message);
    }

    public void sendBookingInvoice(List<Booking> bookings) throws MessagingException {
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("No bookings to process");
        }

        // Create email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true);
        } catch (MessagingException e) {
            throw new RuntimeException("MessageHelper could not be created", e);
        }

        // Get the recipient email and customer name from the first booking
        String recipientEmail = bookings.getFirst().getUser().getEmail(); // Assuming User object has getEmail method
        String customerName = bookings.getFirst().getUser().getFirstName(); // Assuming User object has getFirstName method

        helper.setTo(recipientEmail);
        helper.setSubject("Your UniFit Booking Invoice");

        // Prepare the context for the Thymeleaf template
        Context context = new Context();
        context.setVariable("customerName", customerName);

        double totalPrice = 0.0;

        for (Booking booking : bookings) {
            totalPrice += booking.getFacility().getFacilityType().getRate(); // Accumulate the total price
        }

        // Set the booking details and total price in the context
        context.setVariable("bookings", bookings);
        context.setVariable("totalPrice", totalPrice);

        // Load and populate the Thymeleaf template
        String htmlContent = templateEngine.process("invoice-email", context);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(message);
    }

}
