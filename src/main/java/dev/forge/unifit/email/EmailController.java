package dev.forge.unifit.email;

import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping("/send-email")
    public String sendEmail() {
        try {
            emailService.sendEmail("S11171153@student.usp.ac.fj");
            emailService.sendEmail("S11208749@student.usp.ac.fj");
            emailService.sendEmail("S11209162@student.usp.ac.fj");
            emailService.sendEmail("S11212478@student.usp.ac.fj");
            emailService.sendEmail("20210725@student.unifiji.ac.fj");
            return "Email sent";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
