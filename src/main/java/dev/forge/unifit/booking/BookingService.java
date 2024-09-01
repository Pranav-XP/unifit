package dev.forge.unifit.booking;

import dev.forge.unifit.email.EmailService;
import dev.forge.unifit.facility.Facility;
import dev.forge.unifit.facility.FacilityRepository;
import dev.forge.unifit.facility.FacilityService;

import dev.forge.unifit.notification.NotificationService;
import dev.forge.unifit.user.User;
import dev.forge.unifit.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityService facilityService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public Booking createBooking(BookingFormDTO form) {
        Booking booking = new Booking();
        User user = userService.getUser(form.getUserId());
        booking.setUser(user);
        Facility facility = facilityService.getFacility(form.getFacilityId());
        booking.setFacility(facility);

        booking.setBookedDate(form.getBookedDate());
        booking.setStart(form.getStart());
        booking.setEnd(form.getEnd());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

            BookingNotification notification = new BookingNotification();
            notification.setBookingId(booking.getId());
            notification.setCustomerName(booking.getUser().getFirstName());
            notification.setFacilityName(booking.getFacility().getName());
            notificationService.notifyAdmin(notification);

        return savedBooking;
    }

    @Override
    public Booking setMaintenance(BookingFormDTO form) {
        Booking booking = new Booking();
        User user = userService.getUser(form.getUserId());
        booking.setUser(user);
        Facility facility = facilityService.getFacility(form.getFacilityId());

        booking.setFacility(facility);
        booking.setBookedDate(form.getBookedDate());
        booking.setStart(form.getStart());
        booking.setEnd(form.getEnd());
        booking.setStatus(BookingStatus.MAINTENANCE);

        Booking savedBooking = bookingRepository.save(booking);

        List<Booking> cancelledBookings = bookingRepository.findBookingsBetweenMaintenanceTimes(
                form.getBookedDate(),
                facility,
                form.getStart(),
                form.getEnd());

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

                Thread.sleep(2000);
            } catch (InterruptedException e) {
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
    public Booking getBooking(Long id) {
        return bookingRepository.findById(id).get();
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
