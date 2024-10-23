package dev.forge.unifit.email;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class CancellationEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Method to send Booking Cancellation Email
    public void sendCancellationEmail(String recipientEmail, String bookingDetails, String reason) throws MessagingException {
        Context context = new Context();
        context.setVariable("bookingDetails", bookingDetails);
        context.setVariable("reason", reason);

        String content = templateEngine.process("cancellation-email.html", context);

        sendHtmlEmail(recipientEmail, "Booking Cancellation Notification", content);
    }

    // Helper method to send HTML emails
    private void sendHtmlEmail(String recipientEmail, String subject, String content) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(content, true); // true indicates HTML content

        mailSender.send(helper.getMimeMessage());

    }
}
