package dev.forge.unifit.email;


import dev.forge.unifit.booking.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendBookingInvoice(String recipientEmail, String customerName, Booking booking, double totalPrice) throws MessagingException {
        // Create email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
        } catch (MessagingException e) {
            throw new RuntimeException("MesseageHelper could not be created", e);
        }

        helper.setTo(recipientEmail);
        helper.setSubject("Your UniFit Booking Invoice");

        // Hardcoded mock data
        Context context = new Context();
        context.setVariable("customerName", customerName);
        context.setVariable("booking", booking);
        context.setVariable("totalPrice", "TEST PRICE HOLDER");

        // Load and populate the Thymeleaf template
        String htmlContent = templateEngine.process("invoice-email", context);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(message);
    }
}
