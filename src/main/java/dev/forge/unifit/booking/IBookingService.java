package dev.forge.unifit.booking;

import dev.forge.unifit.facility.FacilityStatus;
import dev.forge.unifit.user.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IBookingService {
    Booking createBooking(BookingFormDTO booking);

    Booking setMaintenance(BookingFormDTO booking);

    List<Booking> getBookingByStatus(BookingStatus status);
    List<Booking> getBookings();

    Booking getBooking(Long id);

    List<Booking> getBookingsByUser(User user);
    List<LocalTime> getAvailableTimes(Long facilityId, LocalDate date);

    Booking saveBooking(Booking booking);

    void cancelBookings(List<Booking> cancelledBookings);

    Page<Booking> getPaginated(int pageNo, int pageSize);
}
