package dev.forge.unifit;

import dev.forge.unifit.booking.Booking;
import dev.forge.unifit.email.EmailService;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.facilitytype.FacilityType;
import dev.forge.unifit.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Email_Testing {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    public void setUp() {
        mimeMessage = mock(MimeMessage.class);
    }

    @Test
    public void testSendBookingInvoice() throws MessagingException {
        // Arrange
        List<Booking> bookings = new ArrayList<>();
        Booking booking = mock(Booking.class);
        User user = mock(User.class);  // Mocking the User object
        Facility facility = mock(Facility.class); // Mocking the Facility
        FacilityType facilityType = mock(FacilityType.class); // Mocking the FacilityType

        // Mock the User object and its methods
        when(user.getEmail()).thenReturn("john@example.com");
        when(user.getFirstName()).thenReturn("John");

        // Mock the Booking to return the mocked User and Facility
        when(booking.getUser()).thenReturn(user);
        when(booking.getFacility()).thenReturn(facility);

        // Mock the Facility to return the mocked FacilityType and its rate
        when(facility.getFacilityType()).thenReturn(facilityType);
        when(facilityType.getRate()).thenReturn(20);  // Integer value

        bookings.add(booking);

        // Mock the creation of the email message
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Mock the template processing
        when(templateEngine.process(eq("invoice-email"), any(Context.class)))
                .thenReturn("<html>Your booking invoice</html>");

        // Act
        emailService.sendBookingInvoice(bookings);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("invoice-email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

}
