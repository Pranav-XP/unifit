package dev.forge.unifit.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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

    public void sendEmail(String toEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("pranavchand777@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject("Your UniFit Booking Invoice - TEST FROM PRANAV");

        Context context = new Context();
        context.setVariable("customerName", "Peter Parker");
        context.setVariable("totalPrice", "15");

        String htmlContent = templateEngine.process("invoice-email", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
