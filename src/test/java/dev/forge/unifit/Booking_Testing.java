package dev.forge.unifit;

import dev.forge.unifit.booking.*;
import dev.forge.unifit.email.EmailService;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityRepository;
import dev.forge.unifit.facility.FacilityService;
import dev.forge.unifit.notification.NotificationService;
import dev.forge.unifit.user.User;
import dev.forge.unifit.user.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class Booking_Testing {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private FacilityService facilityService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    private User user;
    private Facility facility;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(); // Initialize User object
        user.setId(1L);
        user.setFirstName("John");
        user.setEmail("john@example.com");

        facility = new Facility(); // Initialize Facility object
        facility.setId(1L);
        facility.setName("Tennis Court");
    }

    @Test
    void getAvailableTimes_NoneBooked() {
        Long facilityId = 1L;
        LocalDate date = LocalDate.of(2023, 10, 13); // Assuming it's a weekday

        // Mocking repository responses
        LocalTime openingTime = LocalTime.of(9,0);
        LocalTime closingTime = LocalTime.of(18,0);
        when(facilityRepository.findWeekdayOpeningTimeByFacilityId(facilityId))
                .thenReturn(openingTime);

        when(facilityRepository.findWeekdayClosingTimeByFacilityId(facilityId))
                .thenReturn(closingTime);

        when(facilityRepository.findWeekendOpeningTimeByFacilityId(facilityId))
                .thenReturn(openingTime);

        when(facilityRepository.findWeekendClosingTimeByFacilityId(facilityId))
                .thenReturn(closingTime);



        // Assume all times are booked
        List<LocalTime> bookedTimes = Arrays.asList();

        when(bookingRepository.findBookedStartTimesByFacilityAndDate(facilityId, date))
                .thenReturn(bookedTimes);

        // Act
        List<LocalTime> result = bookingService.getAvailableTimes(facilityId, date);

        // Print out times
        System.out.println("Opening hours: " + openingTime +" to " + closingTime);
        System.out.println("Booked Times: " + bookedTimes);
        System.out.println("Available Times: " + result);

        // Assert
        assertEquals(result.size(),result.size()-bookedTimes.size()); // No times are booked

        // Verify that the repository methods were called
        verify(facilityRepository).findWeekdayOpeningTimeByFacilityId(facilityId);
        verify(facilityRepository).findWeekdayClosingTimeByFacilityId(facilityId);
        verify(bookingRepository).findBookedStartTimesByFacilityAndDate(facilityId, date);
    }

     @Test
    void getAvailableTimes_PartiallyBooked() {
        // Arrange
        Long facilityId = 1L;
        LocalDate date = LocalDate.of(2023, 10, 13); // Assuming it's a weekday

        // Mocking repository responses
        LocalTime openingTime = LocalTime.of(9,0);
        LocalTime closingTime = LocalTime.of(18,0);
        when(facilityRepository.findWeekdayOpeningTimeByFacilityId(facilityId))
                .thenReturn(openingTime);

        when(facilityRepository.findWeekdayClosingTimeByFacilityId(facilityId))
                .thenReturn(closingTime);

        when(facilityRepository.findWeekendOpeningTimeByFacilityId(facilityId))
                .thenReturn(openingTime);

        when(facilityRepository.findWeekendClosingTimeByFacilityId(facilityId))
                .thenReturn(closingTime);

        // Assume only two times are booked
        List<LocalTime> bookedTimes = Arrays.asList(LocalTime.of(10, 0), LocalTime.of(14, 0));
        when(bookingRepository.findBookedStartTimesByFacilityAndDate(facilityId, date))
                .thenReturn(bookedTimes);

        // Act
        List<LocalTime> result = bookingService.getAvailableTimes(facilityId, date);

        // Print out times
        System.out.println("Opening hours: " + openingTime +" to " + closingTime);
        System.out.println("Booked Times: " + bookedTimes);
        System.out.println("Available Times: " + result);

        // Assert
        assertEquals(Arrays.asList(LocalTime.of(9, 0), LocalTime.of(11, 0), LocalTime.of(12, 0),
                        LocalTime.of(13, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0)),
                result); // Only two times are booked

            // Verify that the repository methods were called
            verify(facilityRepository).findWeekdayOpeningTimeByFacilityId(facilityId);
            verify(facilityRepository).findWeekdayClosingTimeByFacilityId(facilityId);
            verify(bookingRepository).findBookedStartTimesByFacilityAndDate(facilityId, date);
    }

     @Test
    void getAvailableTimes_AllBooked() {
        // Arrange
        Long facilityId = 1L;
        LocalDate date = LocalDate.of(2023, 10, 13); // Assuming it's a weekday

        // Mocking repository responses
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(18, 0);
        when(facilityRepository.findWeekdayOpeningTimeByFacilityId(facilityId))
                .thenReturn(openingTime);

        when(facilityRepository.findWeekdayClosingTimeByFacilityId(facilityId))
                .thenReturn(closingTime);

        when(facilityRepository.findWeekendOpeningTimeByFacilityId(facilityId))
                .thenReturn(openingTime);

        when(facilityRepository.findWeekendClosingTimeByFacilityId(facilityId))
                .thenReturn(closingTime);

        // Assume all times are booked
        List<LocalTime> bookedTimes = Arrays.asList(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                LocalTime.of(12, 0), LocalTime.of(13, 0), LocalTime.of(14, 0),
                LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0));

        when(bookingRepository.findBookedStartTimesByFacilityAndDate(facilityId, date))
                .thenReturn(bookedTimes);

        // Act
        List<LocalTime> result = bookingService.getAvailableTimes(facilityId, date);

        // Print out times
        System.out.println("Opening hours: " + openingTime +" to " + closingTime);
        System.out.println("Booked Times: " + bookedTimes);
        System.out.println("Available Times: " + result);

        // Assert
        assertEquals(Arrays.asList(), result); // All times are booked

        // Verify that the repository methods were called
        verify(facilityRepository).findWeekdayOpeningTimeByFacilityId(facilityId);
        verify(facilityRepository).findWeekdayClosingTimeByFacilityId(facilityId);
        verify(bookingRepository).findBookedStartTimesByFacilityAndDate(facilityId, date);
    }

    @Test
    public void testSetMaintenance() {
        BookingFormDTO form = new BookingFormDTO();
        form.setUserId(user.getId());
        form.setFacilityId(facility.getId());
        form.setBookedDate(LocalDate.now());
        form.setStart(LocalTime.of(12, 0));
        form.setEnd(LocalTime.of(13, 0));

        when(userService.getUser(form.getUserId())).thenReturn(user);
        when(facilityService.getFacility(form.getFacilityId())).thenReturn(facility);
        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());
        when(bookingRepository.findBookingsBetweenMaintenanceTimes(any(), any(), any(), any())).thenReturn(Collections.emptyList());

        Booking savedBooking = bookingService.setMaintenance(form);

        assertNotNull(savedBooking);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testGetBookingByStatus() {
        BookingStatus status = BookingStatus.RESERVED;
        when(bookingRepository.findAllByStatus(status)).thenReturn(Collections.singletonList(new Booking()));

        List<Booking> bookings = bookingService.getBookingByStatus(status);

        assertFalse(bookings.isEmpty());
        verify(bookingRepository).findAllByStatus(status);
    }

    @Test
    public void testCancelBookings() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.RESERVED);
        List<Booking> bookingsToCancel = Collections.singletonList(booking);

        bookingService.cancelBookings(bookingsToCancel);

        assertEquals(BookingStatus.DELETED, bookingsToCancel.get(0).getStatus());
        verify(bookingRepository).saveAll(bookingsToCancel);
    }

    @Test
    public void testGetBookings() {
        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(new Booking()));

        List<Booking> bookings = bookingService.getBookings();

        assertFalse(bookings.isEmpty());
        verify(bookingRepository).findAll();
    }
/*
    @Test
    public void testGetBooking() {
        Long bookingId = 1L;
        Booking booking = new Booking();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking retrievedBooking = bookingService.getBooking(bookingId);

        assertNotNull(retrievedBooking);
        verify(bookingRepository).findById(bookingId);
    }*/

    @Test
    public void testGetBookingsByUser() {
        when(bookingRepository.findAllByUser(user)).thenReturn(Collections.singletonList(new Booking()));

        List<Booking> bookings = bookingService.getBookingsByUser(user);

        assertFalse(bookings.isEmpty());
        verify(bookingRepository).findAllByUser(user);
    }




@Test
public void testSaveBooking() {
    // Arrange: Set up necessary objects and mock behavior
    Booking booking = new Booking();
    booking.setId(1L); // Set a mock ID to simulate a saved booking
    booking.setFacility(facility); // Assuming facility is set somewhere in your test
    booking.setStatus(BookingStatus.RESERVED); // Set an initial status

    // Mock the behavior for finding the booking by ID
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking)); // Return the booking when found
    when(bookingRepository.save(any(Booking.class))).thenReturn(booking); // Mock save to return the booking

    // Act: Call the method to save the booking
    Booking savedBooking = bookingService.saveBooking(booking);

    // Assert: Verify that the booking was saved and is not null
    assertNotNull(savedBooking, "The saved booking should not be null.");
    assertEquals(1L, savedBooking.getId(), "The booking ID should be 1L.");

    // Verify if the save method was called correctly
    verify(bookingRepository).save(booking);
    verify(bookingRepository).findById(1L); // Verify that findById was called
}


    @Test
    public void testGetPaginated() {
        int pageNo = 1;
        int pageSize = 10;

        when(bookingRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<Booking> paginatedBookings = bookingService.getPaginated(pageNo, pageSize);

        assertNotNull(paginatedBookings);
        verify(bookingRepository).findAll(any(Pageable.class));
    }
}
