package dev.forge.unifit.booking;

import dev.forge.unifit.email.EmailService;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityRepository;
import dev.forge.unifit.facility.FacilityService;

import dev.forge.unifit.notification.NotificationController;
import dev.forge.unifit.notification.NotificationService;
import dev.forge.unifit.transaction.TransactionService;
import dev.forge.unifit.user.User;
import dev.forge.unifit.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityService facilityService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final EmailService emailService;
    private final NotificationController notificationController;

    @Override
    @Transactional
    public List<Booking> createBooking(List<BookingFormDTO> forms) {
        List<Booking> savedBookings = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BookingFormDTO form : forms) {
            Booking booking = new Booking();
            User user = userService.getUser(form.getUserId());
            booking.setUser(user);
            Facility facility = facilityService.getFacility(form.getFacilityId());

            // Check for booking conflict
            Optional<Booking> existingBooking = bookingRepository.findBookingByBookedDateAndStartAndFacility(
                    form.getBookedDate(), form.getStart(), facility.getId());
            if (existingBooking.isPresent()) {
                throw new IllegalStateException("The selected timeslot is already booked for facility: " + facility.getName());
            }

            booking.setCreatedDate(Instant.now());
            booking.setLastModifiedDate(Instant.now());
            booking.setFacility(facility);
            booking.setBookedDate(form.getBookedDate());
            booking.setStart(form.getStart());
            booking.setEnd(form.getEnd());
            booking.setStatus(BookingStatus.RESERVED);

            // Add booking to the list and calculate total amount
            savedBookings.add(booking);
            totalAmount = totalAmount.add(BigDecimal.valueOf(booking.getFacility().getFacilityType().getRate()));
        }

        // Use TransactionService to create and save the transaction with all bookings
        User user = userService.getUser(forms.getFirst().getUserId());
        List<Booking> addedBookings = bookingRepository.saveAll(savedBookings);// Assuming all bookings are for the same user
        transactionService.saveTransactionWithBookings(user, addedBookings, totalAmount);
        notificationController.notifyNewBookings(addedBookings);
        // Send a single email for the entire list
        try {
            emailService.sendBookingInvoice(savedBookings);
        } catch (MessagingException e) {
            throw new RuntimeException("Email not sent for bookings", e);
        }

        return addedBookings;
    }

    @Override
    public Booking setMaintenance(BookingFormDTO form) {
        Booking booking = new Booking();
        User user = userService.getUser(form.getUserId());
        booking.setUser(user);

        Facility facility = facilityService.getFacility(form.getFacilityId());
        booking.setFacility(facility);

        // Set the booked date for the maintenance booking
        LocalDate bookedDate = form.getBookedDate();
        booking.setBookedDate(bookedDate);

        // Set maintenance period for 24 hours based on the booked date
        LocalTime maintenanceStart = bookedDate.atStartOfDay().toLocalTime(); // Start of the booked date
        LocalTime maintenanceEnd = maintenanceStart.plusHours(24); // 24 hours later

        booking.setStart(maintenanceStart);
        booking.setEnd(maintenanceEnd);
        booking.setStatus(BookingStatus.MAINTENANCE);

        // Save the maintenance booking
        Booking savedBooking = bookingRepository.save(booking);

        // Cancel all bookings for the specified booked date
        List<Booking> cancelledBookings = bookingRepository.findBookingByBookedDateAndFacility(bookedDate,facility);

        // Cancel all the bookings found for that date
        cancelBookings(cancelledBookings);

        return savedBooking;
    }


    @Override
    public List<Booking> getBookingByStatus(BookingStatus status) {
        return bookingRepository.findAllByStatus(status);
    }


    @Override
    public void cancelBookings(List<Booking> cancelledBookings) {
        for (Booking booking : cancelledBookings) {
            if (booking.getStatus() != BookingStatus.MAINTENANCE) {
                booking.setStatus(BookingStatus.DELETED);
            }
            try {
                emailService.sendCancellation(booking);
                Thread.sleep(2000);
            } catch (InterruptedException | MessagingException e) {
                throw new RuntimeException(e);
            }
        }
        // Save the updated bookings to the database
        bookingRepository.saveAll(cancelledBookings);
    }

    @Override
    public List<Booking> getBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBooking(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findAllByUser(user);
    }

    @Override
    public List<LocalTime> getAvailableTimes(Long facilityId, LocalDate date) {
        LocalTime openingTime;
        LocalTime closingTime;

        // Determine if it's a weekday or weekend
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        System.out.println("CHECKING AVAILABILITY FOR: " + dayOfWeek);
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            openingTime = facilityRepository.findWeekendOpeningTimeByFacilityId(facilityId);
            closingTime = facilityRepository.findWeekendClosingTimeByFacilityId(facilityId);
        } else {
            openingTime = facilityRepository.findWeekdayOpeningTimeByFacilityId(facilityId);
            closingTime = facilityRepository.findWeekdayClosingTimeByFacilityId(facilityId);
        }
        List<LocalTime> bookedTimes = bookingRepository.findBookedStartTimesByFacilityAndDate(facilityId, date);

        // Logic to calculate available times
        return calculateAvailableTimes(openingTime,closingTime, bookedTimes,date);
    }

    @Override
    public Booking saveBooking(Booking updatedBooking) {
        Booking booking = bookingRepository.findById(updatedBooking.getId()).get();
        booking.setStatus(updatedBooking.getStatus());
        return bookingRepository.save(booking);
    }

    @Override
    public Page<Booking> getPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize, Sort.by("bookedDate").descending());
        return bookingRepository.findAll(pageable);
    }


    private List<LocalTime> calculateAvailableTimes(LocalTime openingTime,LocalTime facilityClosingTime, List<LocalTime> bookedTimes,LocalDate date) {
        // Extract opening and closing times
        LocalTime currentTime = openingTime;
        LocalTime closingTime = facilityClosingTime;
        List<LocalTime> availableTimes = new ArrayList<>();

        //If the current date is picked then Time which has passed should not be considered.
        if(date.isEqual(LocalDate.now())){
            while (currentTime.isBefore(closingTime)) {
                // Check if the currentTime is after the current time
                if (currentTime.isAfter(LocalTime.now())) {
                    availableTimes.add(currentTime);
                }
                currentTime = currentTime.plusHours(1);
            }
            availableTimes.removeAll(bookedTimes);
            return availableTimes;
        }

        while(currentTime.isBefore(closingTime)){
            availableTimes.add(currentTime);
            currentTime = currentTime.plusHours(1);
        }

        availableTimes.removeAll(bookedTimes);

        return availableTimes;
    }
}
